package com.ms.rutas.controller;

import com.ms.rutas.model.RutaModel;
import com.ms.rutas.service.RutaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService service;

    @GetMapping
    public List<RutaModel> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public RutaModel buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<RutaModel> crear(
            @Valid @RequestBody RutaModel ruta
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(ruta));
    }

    @PutMapping("/{id}")
    public RutaModel actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RutaModel ruta
    ) {
        return service.actualizar(id, ruta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
