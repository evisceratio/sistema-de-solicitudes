package com.ms.auth.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank String correo,
        @NotBlank String clave
) {}