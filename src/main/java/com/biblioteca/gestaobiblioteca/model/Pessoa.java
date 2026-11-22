package com.biblioteca.gestaobiblioteca.model;

import java.time.LocalDate;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

@MappedSuperclass
public abstract class Pessoa {

    @NotBlank
    private String nome;

    @NotNull
    @Past
    private LocalDate dataNascimento;

    @NotBlank
    private String telefone;

    protected Pessoa() {
    }

    protected Pessoa(String nome, LocalDate dataNascimento, String telefone) {
        validar(nome, dataNascimento, telefone);
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
    }

    public void atualizar(String nome, LocalDate dataNascimento, String telefone) {
        validar(nome, dataNascimento, telefone);
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
    }

    private void validar(String nome, LocalDate dataNascimento, String telefone) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        if (dataNascimento == null || !dataNascimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento deve ser no passado.");
        }
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone não pode ser vazio.");
        }
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public String exibirInformacoes() {
        return "Nome: " + nome + " | Nascimento: " + dataNascimento
                + " | Telefone: " + telefone;
    }
}