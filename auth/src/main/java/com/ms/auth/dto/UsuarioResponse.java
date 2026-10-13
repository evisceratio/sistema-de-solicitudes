package com.ms.auth.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String rol
) {}