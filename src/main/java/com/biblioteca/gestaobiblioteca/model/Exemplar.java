package com.biblioteca.gestaobiblioteca.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;


@Entity
public class Exemplar {

    @Id
    @Positive
    private int codigo;

    @Enumerated(EnumType.STRING)
    private StatusExemplar status = StatusExemplar.DISPONIVEL;

    @ManyToOne
    @JsonBackReference
    private Livro livro;

    public Exemplar(int codigo) {

        if (codigo <= 0) {
            throw new IllegalArgumentException(
                    "O código do exemplar deve ser maior que zero.");
        }

        this.codigo = codigo;

        // Todo novo exemplar começa disponível
        this.status = StatusExemplar.DISPONIVEL;
    }

    public int getCodigo() {
        return codigo;
    }

    public StatusExemplar getStatus() {
        return status;
    }

    protected Exemplar() {
    }

    public void emprestar() {
        this.status = StatusExemplar.EMPRESTADO;
    }

    public void devolver() {
        this.status = StatusExemplar.DISPONIVEL;
    }

    void setLivro(Livro livro) {
        this.livro = livro;
    }

    Livro getLivro() {
        return livro;
    }

    // Getters calculados: o JSON mostra o livro sem aninhar objetos
    public Integer getCodigoLivro() {
        return livro == null ? null : livro.getCodigo();
    }

    public String getTituloLivro() {
        return livro == null ? null : livro.getTitulo();
    }

}