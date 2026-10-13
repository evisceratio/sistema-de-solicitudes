package com.ms.flota.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "vehiculos")
@Getter
@Setter
@NoArgsConstructor
public class VehiculoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String patente;

    @NotBlank
    private String modelo;

    @NotBlank
    private String estado;
}