package com.biblioteca.gestaobiblioteca.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.gestaobiblioteca.dto.LoginRequest;
import com.biblioteca.gestaobiblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.gestaobiblioteca.model.Funcionario;
import com.biblioteca.gestaobiblioteca.model.Usuario;
import com.biblioteca.gestaobiblioteca.repository.FuncionarioRepository;
import com.biblioteca.gestaobiblioteca.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;

    public LoginController(UsuarioRepository usuarioRepository,
                           FuncionarioRepository funcionarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @PostMapping
    public Usuario login(@Valid @RequestBody LoginRequest dados) {
        Usuario usuario = usuarioRepository.findById(dados.registro())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Registro ou senha inválidos."));
        if (!usuario.senhaConfere(dados.senha())) {
            throw new RecursoNaoEncontradoException("Registro ou senha inválidos.");
        }
        return usuario;
    }

    // No LoginRequest, o campo "registro" carrega o ID funcional
    @PostMapping("/funcionario")
    public Funcionario loginFuncionario(@Valid @RequestBody LoginRequest dados) {
        Funcionario funcionario = funcionarioRepository.findById(dados.registro())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "ID ou senha inválidos."));
        if (!funcionario.senhaConfere(dados.senha())) {
            throw new RecursoNaoEncontradoException("ID ou senha inválidos.");
        }
        return funcionario;
    }
}