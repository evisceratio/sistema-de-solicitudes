package com.ms.solicitudes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import lombok.*;

@Document(collection = "solicitudes")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudModel {

    @Id
    private String id;

    @NotNull
    private Long vecinoId;

    @NotNull
    private Long rutaId;

    @NotNull
    private Long vehiculoId;

    @NotBlank
    private String direccion;

    private String estado;
}