package com.ms.flota.repository;

import com.ms.flota.model.MovimientoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository
        extends JpaRepository<MovimientoModel, Long> {

    boolean existsByVehiculoId(Long vehiculoId);
    boolean existsBySolicitudId(String solicitudId);
}