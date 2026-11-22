package com.biblioteca.gestaobiblioteca.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
public class Usuario extends Pessoa {

    @Id
    @Positive
    private int registro;

    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    public boolean senhaConfere(String tentativa) {
        return senha != null && senha.equals(tentativa);
    }

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<Emprestimo> emprestimos = new ArrayList<>();

        public Usuario(int registro, String nome, LocalDate dataNascimento,
                   String telefone, String senha) {
        super(nome, dataNascimento, telefone);
        if (registro <= 0) {
            throw new IllegalArgumentException(
                    "O registro do usuário deve ser maior que zero.");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        this.registro = registro;
        this.senha = senha;
        gerarEmail();
    }

    protected Usuario() {
    }

    @PrePersist
    private void gerarEmail() {
        this.email = registro + "@biblioteca.com";
    }

    public int getRegistro() {
        return registro;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String exibirInformacoes() {
        return "Usuário " + registro + " | " + super.exibirInformacoes()
                + " | Email: " + email;
    }

    public List<Emprestimo> getEmprestimos() {
        return new ArrayList<>(emprestimos);
    }
}