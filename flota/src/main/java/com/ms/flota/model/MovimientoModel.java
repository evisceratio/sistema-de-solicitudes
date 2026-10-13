package com.ms.flota.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "movimientos")
@Getter
@Setter
@NoArgsConstructor
public class MovimientoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String solicitudId;
    private Long rutaId;
    private Instant fecha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private VehiculoModel vehiculo;
}