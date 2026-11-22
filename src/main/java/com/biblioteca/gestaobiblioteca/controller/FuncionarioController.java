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
import com.biblioteca.gestaobiblioteca.model.Funcionario;
import com.biblioteca.gestaobiblioteca.repository.FuncionarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioController(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @GetMapping
    public List<Funcionario> listar() {
        return funcionarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Funcionario buscar(@PathVariable int id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Funcionário não encontrado: " + id));
    }

    @GetMapping("/{id}/informacoes")
    public String informacoes(@PathVariable int id) {
        return buscar(id).exibirInformacoes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Funcionario criar(@Valid @RequestBody Funcionario funcionario) {
        if (funcionarioRepository.existsById(funcionario.getId())) {
            throw new CodigoDuplicadoException(
                    "Já existe um funcionário com o id " + funcionario.getId());
        }
        return funcionarioRepository.save(funcionario);
    }

    @PutMapping("/{id}")
    public Funcionario atualizar(@PathVariable int id,
                                 @Valid @RequestBody Funcionario dados) {
        Funcionario funcionario = buscar(id);
        funcionario.atualizar(dados.getNome(), dados.getDataNascimento(), dados.getTelefone());
        return funcionarioRepository.save(funcionario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable int id) {
        buscar(id);
        funcionarioRepository.deleteById(id);
    }
}