package com.ms.auth.repository;

import com.ms.auth.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<UsuarioModel, Long> {

    Optional<UsuarioModel> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}

