package com.enriquehs.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductRequest(
    @NotBlank(message = "Nombre es obligatorio")
    String name,

    @NotNull(message = "Precio es obligatorio")
    @Positive(message = "Precio debe ser mayor que cero")
    BigDecimal price
) {
}
