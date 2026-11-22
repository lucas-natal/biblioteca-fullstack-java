package com.biblioteca.gestaobiblioteca.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    private Exemplar exemplar;

    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal;

    protected Emprestimo() {
    }

    // Sobrecarga: sem data informada, usa a data de hoje
    public Emprestimo(Usuario usuario, Exemplar exemplar) {
        this(usuario, exemplar, LocalDate.now());
    }

    public Emprestimo(Usuario usuario, Exemplar exemplar, LocalDate dataEmprestimo) {
        this.usuario = usuario;
        this.exemplar = exemplar;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataEmprestimo.plusDays(15);
    }

    public void devolver() {
        this.dataDevolucaoReal = LocalDate.now();
        this.exemplar.devolver();
    }

    public boolean isAtivo() {
        return dataDevolucaoReal == null;
    }

    public boolean isAtrasado() {
        return isAtivo() && LocalDate.now().isAfter(dataDevolucaoPrevista);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    // Getters calculados: o JSON mostra só o necessário, sem aninhar objetos
    public int getRegistroUsuario() {
        return usuario.getRegistro();
    }

    public String getNomeUsuario() {
        return usuario.getNome();
    }

    public int getCodigoExemplar() {
        return exemplar.getCodigo();
    }

    public String getTituloLivro() {
        return exemplar.getLivro().getTitulo();
    }
}