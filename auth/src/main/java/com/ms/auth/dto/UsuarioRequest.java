package com.ms.auth.dto;

import jakarta.validation.constraints.*;

public record UsuarioRequest(
        @NotBlank String nombre,
        @NotBlank @Email String correo,
        @NotBlank String clave
) {}