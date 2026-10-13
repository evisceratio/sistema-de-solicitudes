package com.ms.flota.service;

import com.ms.flota.dto.MovimientoRequest;
import com.ms.flota.model.MovimientoModel;
import com.ms.flota.model.VehiculoModel;
import com.ms.flota.repository.MovimientoRepository;
import com.ms.flota.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlotaService {

    private final VehiculoRepository vehiculos;
    private final MovimientoRepository movimientos;

    // CRUD VEHICULOS

    public List<VehiculoModel> listarVehiculos() {
        return vehiculos.findAll();
    }

    public VehiculoModel buscarVehiculo(Long id) {
        return vehiculos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehiculo no encontrado"));
    }

    public VehiculoModel crearVehiculo(VehiculoModel datos) {

        if (vehiculos.existsByPatente(datos.getPatente())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Patente ya registrada");
        }

        datos.setId(null);
        return vehiculos.save(datos);
    }

    public VehiculoModel actualizarVehiculo(
            Long id, VehiculoModel datos
    ) {
        VehiculoModel actual = buscarVehiculo(id);

        if (!actual.getPatente().equals(datos.getPatente())
                && vehiculos.existsByPatente(datos.getPatente())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Patente ya registrada");
        }

        actual.setPatente(datos.getPatente());
        actual.setModelo(datos.getModelo());
        actual.setEstado(datos.getEstado());

        return vehiculos.save(actual);
    }

    public void eliminarVehiculo(Long id) {
        VehiculoModel actual = buscarVehiculo(id);

        if (movimientos.existsByVehiculoId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El vehiculo tiene movimientos asociados");
        }

        vehiculos.delete(actual);
    }

    // CRUD MOVIMIENTOS

    public List<MovimientoModel> listarMovimientos() {
        return movimientos.findAll();
    }

    public MovimientoModel buscarMovimiento(Long id) {
        return movimientos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Movimiento no encontrado"));
    }

    public MovimientoModel crearMovimiento(
            MovimientoRequest datos
    ) {
        VehiculoModel vehiculo = buscarVehiculo(datos.vehiculoId());

        MovimientoModel movimiento = new MovimientoModel();
        movimiento.setSolicitudId(datos.solicitudId());
        movimiento.setRutaId(datos.rutaId());
        movimiento.setVehiculo(vehiculo);
        movimiento.setFecha(Instant.now());

        return movimientos.save(movimiento);
    }

    public MovimientoModel actualizarMovimiento(
            Long id, MovimientoRequest datos
    ) {
        MovimientoModel actual = buscarMovimiento(id);
        VehiculoModel vehiculo = buscarVehiculo(datos.vehiculoId());

        actual.setSolicitudId(datos.solicitudId());
        actual.setRutaId(datos.rutaId());
        actual.setVehiculo(vehiculo);

        return movimientos.save(actual);
    }

    public void eliminarMovimiento(Long id) {
        MovimientoModel actual = buscarMovimiento(id);
        movimientos.delete(actual);
    }

    // Se usara posteriormente con RabbitMQ

    public void registrarDesdeEvento(
            MovimientoRequest datos
    ) {
        if (!movimientos.existsBySolicitudId(
                datos.solicitudId())) {
            crearMovimiento(datos);
        }
    }
}