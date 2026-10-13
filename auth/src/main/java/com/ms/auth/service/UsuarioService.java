package com.ms.auth.service;

import com.ms.auth.dto.LoginRequest;
import com.ms.auth.dto.TokenResponse;
import com.ms.auth.dto.UsuarioRequest;
import com.ms.auth.dto.UsuarioResponse;
import com.ms.auth.model.UsuarioModel;
import com.ms.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    private UsuarioModel buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private UsuarioResponse convertir(UsuarioModel u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getCorreo(),
                u.getRol()
        );
    }

    public List<UsuarioResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    public UsuarioResponse buscar(Long id) {
        return convertir(buscarEntidad(id));
    }

    public UsuarioResponse crear(UsuarioRequest datos) {

        if (repository.existsByCorreo(datos.correo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Correo ya registrado");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNombre(datos.nombre());
        usuario.setCorreo(datos.correo());
        usuario.setClaveHash(
                passwordEncoder.encode(datos.clave())
        );
        usuario.setRol("VECINO");

        return convertir(repository.save(usuario));
    }

    public UsuarioResponse actualizar(
            Long id, UsuarioRequest datos
    ) {
        UsuarioModel usuario = buscarEntidad(id);

        repository.findByCorreo(datos.correo())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Correo ya registrado");
                    }
                });

        usuario.setNombre(datos.nombre());
        usuario.setCorreo(datos.correo());
        usuario.setClaveHash(
                passwordEncoder.encode(datos.clave())
        );

        return convertir(repository.save(usuario));
    }

    public void eliminar(Long id) {
        UsuarioModel usuario = buscarEntidad(id);
        repository.delete(usuario);
    }

    public TokenResponse login(LoginRequest datos) {

        UsuarioModel usuario = repository
                .findByCorreo(datos.correo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales incorrectas"));

        if (!passwordEncoder.matches(
                datos.clave(), usuario.getClaveHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas");
        }

        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getCorreo())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(2, ChronoUnit.HOURS))
                .claim("rol", usuario.getRol())
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new TokenResponse(token);
    }
}