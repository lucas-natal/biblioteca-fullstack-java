package com.biblioteca.gestaobiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.gestaobiblioteca.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}