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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.gestaobiblioteca.exception.CodigoDuplicadoException;
import com.biblioteca.gestaobiblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.gestaobiblioteca.model.Usuario;
import com.biblioteca.gestaobiblioteca.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{registro}")
    public Usuario buscar(@PathVariable int registro) {
        return usuarioRepository.findById(registro)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado: " + registro));
    }

    @GetMapping("/{registro}/informacoes")
    public String informacoes(@PathVariable int registro) {
        return buscar(registro).exibirInformacoes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario criar(@Valid @RequestBody Usuario usuario) {
        if (usuarioRepository.existsById(usuario.getRegistro())) {
            throw new CodigoDuplicadoException(
                    "Já existe um usuário com o registro " + usuario.getRegistro());
        }
        return usuarioRepository.save(usuario);
    }

    @PutMapping("/{registro}")
    public Usuario atualizar(@PathVariable int registro,
                             @Valid @RequestBody Usuario dados) {
        Usuario usuario = buscar(registro);
        usuario.atualizar(dados.getNome(), dados.getDataNascimento(), dados.getTelefone());
        return usuarioRepository.save(usuario);
    }

    @DeleteMapping("/{registro}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable int registro) {
        buscar(registro);
        usuarioRepository.deleteById(registro);
    }
}