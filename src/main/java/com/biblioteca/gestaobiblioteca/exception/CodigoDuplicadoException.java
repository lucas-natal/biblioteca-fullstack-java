package com.biblioteca.gestaobiblioteca.exception;

public class CodigoDuplicadoException extends RuntimeException {
    public CodigoDuplicadoException(String mensagem) {
        super(mensagem);
    }
}