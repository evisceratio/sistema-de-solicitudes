package com.ms.rutas.service;

import com.ms.rutas.model.ParadaModel;
import com.ms.rutas.model.RutaModel;
import com.ms.rutas.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RutaService {

    private final RutaRepository repository;

    public List<RutaModel> listar() {
        return repository.findAll();
    }

    public RutaModel buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ruta no encontrada"));
    }

    @Transactional
    public RutaModel crear(RutaModel datos) {
        datos.setId(null);

        if (datos.getParadas() == null) {
            datos.setParadas(new ArrayList<>());
        }

        for (ParadaModel parada : datos.getParadas()) {
            parada.setId(null);
            parada.setRuta(datos);
        }

        return repository.save(datos);
    }

    @Transactional
    public RutaModel actualizar(Long id, RutaModel datos) {
        RutaModel actual = buscar(id);

        actual.setNombre(datos.getNombre());
        actual.setOrigen(datos.getOrigen());
        actual.setDestino(datos.getDestino());

        actual.getParadas().clear();

        if (datos.getParadas() != null) {
            for (ParadaModel p : datos.getParadas()) {
                ParadaModel nueva = new ParadaModel();
                nueva.setNombre(p.getNombre());
                nueva.setOrden(p.getOrden());
                nueva.setRuta(actual);

                actual.getParadas().add(nueva);
            }
        }

        return repository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        RutaModel ruta = buscar(id);
        repository.delete(ruta);
    }
}
