package com.ms.rutas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ruta")
@Getter
@Setter
@NoArgsConstructor
public class RutaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String origen;

    @NotBlank
    private String destino;

    @OneToMany(
            mappedBy = "ruta",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<ParadaModel> paradas = new ArrayList<>();
}