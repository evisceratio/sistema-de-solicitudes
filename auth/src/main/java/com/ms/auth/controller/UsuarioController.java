package com.ms.auth.controller;

import com.ms.auth.dto.LoginRequest;
import com.ms.auth.dto.TokenResponse;
import com.ms.auth.dto.UsuarioRequest;
import com.ms.auth.dto.UsuarioResponse;
import com.ms.auth.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping("/api/usuarios")
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @GetMapping("/api/usuarios/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping("/api/usuarios")
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody UsuarioRequest datos
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(datos));
    }

    @PutMapping("/api/usuarios/{id}")
    public UsuarioResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest datos
    ) {
        return service.actualizar(id, datos);
    }

    @DeleteMapping("/api/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PostMapping("/api/auth/login")
    public TokenResponse login(
            @Valid @RequestBody LoginRequest datos
    ) {
        return service.login(datos);
    }
}
