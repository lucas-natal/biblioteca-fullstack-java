package com.biblioteca.gestaobiblioteca.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity 
public class Livro {
    @Id
    @Positive
    private int codigo;

    @NotBlank
    private String titulo;

    @NotBlank
    private String autor;

    @Positive
    private int anoPublicacao;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Exemplar> exemplares = new ArrayList<>();

    public Livro(int codigo, String titulo, String autor, int anoPublicacao) {

        if (codigo <= 0) {
            throw new IllegalArgumentException(
                    "O código do livro deve ser maior que zero."
            );
        }

        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "O título não pode ser vazio."
            );
        }

        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "O autor não pode ser vazio."
            );
        }

        if (anoPublicacao <= 0) {
            throw new IllegalArgumentException(
                    "O ano de publicação deve ser válido."
            );
        }

        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.exemplares = new ArrayList<>();
    }

    public int getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public List<Exemplar> getExemplares() {
        return new ArrayList<>(exemplares);
    }

    // Sobrecarga de métodos para adicionar exemplares
    public void adicionarExemplar(Exemplar exemplar) {
        exemplar.setLivro(this);
        exemplares.add(exemplar);
    }

    public void adicionarExemplar(int codigoExemplar) {
        Exemplar exemplar = new Exemplar(codigoExemplar);
        exemplar.setLivro(this);
        exemplares.add(exemplar);
    }

    protected Livro() {
        
    }

    public void atualizar(String titulo, String autor, int anoPublicacao) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título não pode ser vazio.");
        }
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("O autor não pode ser vazio.");
        }
        if (anoPublicacao <= 0) {
            throw new IllegalArgumentException("O ano de publicação deve ser válido.");
        }
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
    }
}