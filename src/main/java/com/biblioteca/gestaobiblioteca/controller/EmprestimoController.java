package com.biblioteca.gestaobiblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.gestaobiblioteca.dto.EmprestimoRequest;
import com.biblioteca.gestaobiblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.gestaobiblioteca.exception.RegraNegocioException;
import com.biblioteca.gestaobiblioteca.model.Emprestimo;
import com.biblioteca.gestaobiblioteca.model.Exemplar;
import com.biblioteca.gestaobiblioteca.model.StatusExemplar;
import com.biblioteca.gestaobiblioteca.model.Usuario;
import com.biblioteca.gestaobiblioteca.repository.EmprestimoRepository;
import com.biblioteca.gestaobiblioteca.repository.ExemplarRepository;
import com.biblioteca.gestaobiblioteca.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ExemplarRepository exemplarRepository;

    public EmprestimoController(EmprestimoRepository emprestimoRepository,
                                UsuarioRepository usuarioRepository,
                                ExemplarRepository exemplarRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.usuarioRepository = usuarioRepository;
        this.exemplarRepository = exemplarRepository;
    }

    @GetMapping
    public List<Emprestimo> listar() {
        return emprestimoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Emprestimo buscar(@PathVariable Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Empréstimo não encontrado: " + id));
    }

    @GetMapping("/usuario/{registro}")
    public List<Emprestimo> listarDoUsuario(@PathVariable int registro) {
        Usuario usuario = usuarioRepository.findById(registro)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado: " + registro));
        return usuario.getEmprestimos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Emprestimo emprestar(@Valid @RequestBody EmprestimoRequest dados) {
        Usuario usuario = usuarioRepository.findById(dados.registroUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado: " + dados.registroUsuario()));

        Exemplar exemplar = exemplarRepository.findById(dados.codigoExemplar())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Exemplar não encontrado: " + dados.codigoExemplar()));

        if (exemplar.getStatus() == StatusExemplar.EMPRESTADO) {
            throw new RegraNegocioException("O exemplar "
                    + exemplar.getCodigo() + " já está emprestado.");
        }

        exemplar.emprestar();
        exemplarRepository.save(exemplar);
        return emprestimoRepository.save(new Emprestimo(usuario, exemplar));
    }

    @PutMapping("/{id}/devolucao")
    @Transactional
    public Emprestimo devolver(@PathVariable Long id) {
        Emprestimo emprestimo = buscar(id);

        if (!emprestimo.isAtivo()) {
            throw new RegraNegocioException("Este empréstimo já foi devolvido.");
        }

        emprestimo.devolver();
        return emprestimoRepository.save(emprestimo);
    }
}