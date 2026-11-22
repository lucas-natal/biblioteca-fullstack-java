package com.biblioteca.gestaobiblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.gestaobiblioteca.exception.CodigoDuplicadoException;
import com.biblioteca.gestaobiblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.gestaobiblioteca.model.Livro;
import com.biblioteca.gestaobiblioteca.repository.LivroRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @GetMapping
    public List<Livro> listar() {
        return livroRepository.findAll();
    }

    @GetMapping("/{codigo}")
    public Livro buscar(@PathVariable int codigo) {
        return livroRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Livro não encontrado: " + codigo));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Livro criar(@Valid @RequestBody Livro livro) {
        if (livroRepository.existsById(livro.getCodigo())) {
            throw new CodigoDuplicadoException(
                    "Já existe um livro com o código " + livro.getCodigo());
        }
        return livroRepository.save(livro);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable int codigo) {
        buscar(codigo);
        livroRepository.deleteById(codigo);
    }

    @PutMapping("/{codigo}")
    public Livro atualizar(@PathVariable int codigo, @Valid @RequestBody Livro dados) {
        Livro livro = buscar(codigo);
        livro.atualizar(dados.getTitulo(), dados.getAutor(), dados.getAnoPublicacao());
        return livroRepository.save(livro);
    }

    @GetMapping("/busca")
    public List<Livro> buscarPorTitulo(@RequestParam String titulo) {
        return livroRepository.findByTituloContainingIgnoreCase(titulo);
    }
}