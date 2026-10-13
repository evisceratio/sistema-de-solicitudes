package com.ms.flota.controller;

import com.ms.flota.dto.MovimientoRequest;
import com.ms.flota.model.MovimientoModel;
import com.ms.flota.model.VehiculoModel;
import com.ms.flota.service.FlotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flota")
@RequiredArgsConstructor
public class FlotaController {

    private final FlotaService service;

    // VEHICULOS

    @GetMapping
    public List<VehiculoModel> listarVehiculos() {
        return service.listarVehiculos();
    }

    @GetMapping("/{id}")
    public VehiculoModel buscarVehiculo(@PathVariable Long id) {
        return service.buscarVehiculo(id);
    }

    @PostMapping
    public ResponseEntity<VehiculoModel> crearVehiculo(
            @Valid @RequestBody VehiculoModel vehiculo
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crearVehiculo(vehiculo));
    }

    @PutMapping("/{id}")
    public VehiculoModel actualizarVehiculo(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoModel vehiculo
    ) {
        return service.actualizarVehiculo(id, vehiculo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarVehiculo(@PathVariable Long id) {
        service.eliminarVehiculo(id);
    }

    // MOVIMIENTOS

    @GetMapping("/movimientos")
    public List<MovimientoModel> listarMovimientos() {
        return service.listarMovimientos();
    }

    @GetMapping("/movimientos/{id}")
    public MovimientoModel buscarMovimiento(
            @PathVariable Long id
    ) {
        return service.buscarMovimiento(id);
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoModel> crearMovimiento(
            @Valid @RequestBody MovimientoRequest datos
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crearMovimiento(datos));
    }

    @PutMapping("/movimientos/{id}")
    public MovimientoModel actualizarMovimiento(
            @PathVariable Long id,
            @Valid @RequestBody MovimientoRequest datos
    ) {
        return service.actualizarMovimiento(id, datos);
    }

    @DeleteMapping("/movimientos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarMovimiento(
            @PathVariable Long id
    ) {
        service.eliminarMovimiento(id);
    }
}