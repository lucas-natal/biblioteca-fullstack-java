package com.biblioteca.gestaobiblioteca.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
public class Funcionario extends Pessoa {

    @Id
    @Positive
    private int id;

    private String email;

    // A senha só entra no JSON (cadastro/login), nunca é devolvida pela API
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    public Funcionario(int id, String nome, LocalDate dataNascimento,
                       String telefone, String senha) {
        super(nome, dataNascimento, telefone);
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "O id do funcionário deve ser maior que zero.");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        this.id = id;
        this.senha = senha;
        gerarEmail();
    }

    protected Funcionario() {
    }

    public boolean senhaConfere(String tentativa) {
        return senha != null && senha.equals(tentativa);
    }

    @PrePersist
    private void gerarEmail() {
        this.email = id + "@biblioteca.com";
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String exibirInformacoes() {
        return "Funcionário " + id + " | " + super.exibirInformacoes()
                + " | Email: " + email;
    }
}