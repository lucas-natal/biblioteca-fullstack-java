package com.biblioteca.gestaobiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.gestaobiblioteca.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

}
