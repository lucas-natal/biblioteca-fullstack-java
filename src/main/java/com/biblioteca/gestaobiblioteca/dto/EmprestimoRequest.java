package com.biblioteca.gestaobiblioteca.dto;

import jakarta.validation.constraints.Positive;

public record EmprestimoRequest(@Positive int registroUsuario, @Positive int codigoExemplar) {

}