package com.ms.rutas.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Entity
@Table(name = "parada")
@Getter
@Setter
@NoArgsConstructor
public class ParadaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "orden_parada")
    private Integer orden;

    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    @JsonIgnore
    private RutaModel ruta;
}