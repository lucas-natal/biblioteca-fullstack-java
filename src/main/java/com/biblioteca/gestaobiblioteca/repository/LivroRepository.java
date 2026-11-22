package com.biblioteca.gestaobiblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.gestaobiblioteca.model.Livro;

public interface LivroRepository extends JpaRepository<Livro, Integer> {
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
}