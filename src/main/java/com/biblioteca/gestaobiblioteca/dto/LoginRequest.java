package com.biblioteca.gestaobiblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record LoginRequest(@Positive int registro, @NotBlank String senha) {
}