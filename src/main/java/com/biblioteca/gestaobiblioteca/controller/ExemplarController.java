package com.biblioteca.gestaobiblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.gestaobiblioteca.exception.CodigoDuplicadoException;
import com.biblioteca.gestaobiblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.gestaobiblioteca.exception.RegraNegocioException;
import com.biblioteca.gestaobiblioteca.model.Exemplar;
import com.biblioteca.gestaobiblioteca.model.Livro;
import com.biblioteca.gestaobiblioteca.model.StatusExemplar;
import com.biblioteca.gestaobiblioteca.repository.EmprestimoRepository;
import com.biblioteca.gestaobiblioteca.repository.ExemplarRepository;
import com.biblioteca.gestaobiblioteca.repository.LivroRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ExemplarController {

    private final LivroRepository livroRepository;
    private final ExemplarRepository exemplarRepository;
    private final EmprestimoRepository emprestimoRepository;

    public ExemplarController(LivroRepository livroRepository,
                              ExemplarRepository exemplarRepository,
                              EmprestimoRepository emprestimoRepository) {
        this.livroRepository = livroRepository;
        this.exemplarRepository = exemplarRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @PostMapping("/livros/{codigoLivro}/exemplares")
    @ResponseStatus(HttpStatus.CREATED)
    public Exemplar cadastrar(@PathVariable int codigoLivro,
                              @Valid @RequestBody Exemplar exemplar) {
        Livro livro = buscarLivro(codigoLivro);

        if (exemplarRepository.existsById(exemplar.getCodigo())) {
            throw new CodigoDuplicadoException(
                    "Já existe um exemplar com o código " + exemplar.getCodigo());
        }

        livro.adicionarExemplar(exemplar);
        livroRepository.save(livro);
        return exemplar;
    }

    @GetMapping("/exemplares/{codigo}")
    public Exemplar buscar(@PathVariable int codigo) {
        return exemplarRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Exemplar não encontrado: " + codigo));
    }

    @GetMapping("/livros/{codigoLivro}/exemplares/disponiveis")
    public List<Exemplar> listarDisponiveis(@PathVariable int codigoLivro) {
        return buscarLivro(codigoLivro).getExemplares().stream()
                .filter(e -> e.getStatus() == StatusExemplar.DISPONIVEL)
                .toList();
    }

    @DeleteMapping("/exemplares/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable int codigo) {
        Exemplar exemplar = buscar(codigo);
        if (exemplar.getStatus() == StatusExemplar.EMPRESTADO) {
            throw new RegraNegocioException("O exemplar " + codigo
                    + " está emprestado e não pode ser removido.");
        }
        if (emprestimoRepository.existsByExemplarCodigo(codigo)) {
            throw new RegraNegocioException("O exemplar " + codigo
                    + " tem histórico de empréstimos e não pode ser removido.");
        }
        exemplarRepository.delete(exemplar);
    }

    private Livro buscarLivro(int codigo) {
        return livroRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Livro não encontrado: " + codigo));
    }
}