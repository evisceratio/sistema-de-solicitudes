package com.ms.solicitudes.service;


import com.ms.solicitudes.model.SolicitudModel;
import com.ms.solicitudes.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudRepository repository;

    public List<SolicitudModel> listar() {
        return repository.findAll();
    }

    public SolicitudModel buscar(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitud no encontrada"));
    }

    public SolicitudModel crear(SolicitudModel solicitud) {
        solicitud.setId(null);
        solicitud.setEstado("PENDIENTE");

        return repository.save(solicitud);
    }

    public SolicitudModel actualizar(
            String id, SolicitudModel datos
    ) {
        SolicitudModel actual = buscar(id);

        actual.setVecinoId(datos.getVecinoId());
        actual.setRutaId(datos.getRutaId());
        actual.setVehiculoId(datos.getVehiculoId());
        actual.setDireccion(datos.getDireccion());

        if (datos.getEstado() != null) {
            actual.setEstado(datos.getEstado());
        }

        return repository.save(actual);
    }

    public void eliminar(String id) {
        SolicitudModel actual = buscar(id);
        repository.delete(actual);
    }
}
