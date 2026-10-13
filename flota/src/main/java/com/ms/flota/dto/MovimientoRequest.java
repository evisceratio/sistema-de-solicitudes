package com.ms.flota.dto;

import jakarta.validation.constraints.*;

public record MovimientoRequest(
        @NotBlank String solicitudId,
        @NotNull Long rutaId,
        @NotNull Long vehiculoId
) {}