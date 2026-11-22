package com.biblioteca.gestaobiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.gestaobiblioteca.model.Exemplar;

public interface ExemplarRepository extends JpaRepository<Exemplar, Integer> {
}