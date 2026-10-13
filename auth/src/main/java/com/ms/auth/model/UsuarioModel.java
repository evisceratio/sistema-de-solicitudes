package com.ms.auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String claveHash;

    @Column(nullable = false)
    private String rol;
}