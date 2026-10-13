package com.ms.rutas.repository;

import com.ms.rutas.model.RutaModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaRepository
        extends JpaRepository<RutaModel, Long> {
}