package com.ms.solicitudes.controller;

import com.ms.solicitudes.model.SolicitudModel;
import com.ms.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService service;

    @GetMapping
    public List<SolicitudModel> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public SolicitudModel buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<SolicitudModel> crear(
            @Valid @RequestBody SolicitudModel solicitud
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(solicitud));
    }

    @PutMapping("/{id}")
    public SolicitudModel actualizar(
            @PathVariable String id,
            @Valid @RequestBody SolicitudModel solicitud
    ) {
        return service.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminar(id);
    }
}