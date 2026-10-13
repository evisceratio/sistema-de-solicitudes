package com.ms.solicitudes.repository;

import com.ms.solicitudes.model.SolicitudModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SolicitudRepository
        extends MongoRepository<SolicitudModel, String> {
}
