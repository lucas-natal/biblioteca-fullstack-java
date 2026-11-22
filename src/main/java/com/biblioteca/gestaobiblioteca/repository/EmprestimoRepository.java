package com.biblioteca.gestaobiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.gestaobiblioteca.model.Emprestimo;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // Consulta derivada do nome: existe empréstimo cujo exemplar tem este código?
    boolean existsByExemplarCodigo(int codigo);
}