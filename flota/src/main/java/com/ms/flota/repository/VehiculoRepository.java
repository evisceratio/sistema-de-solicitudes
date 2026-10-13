package com.ms.flota.repository;

import com.ms.flota.model.VehiculoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository
        extends JpaRepository<VehiculoModel, Long> {

    boolean existsByPatente(String patente);
}