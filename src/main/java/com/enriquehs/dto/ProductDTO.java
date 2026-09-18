package com.enriquehs.dto;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record ProductDTO (
    Long id,
    
    @NotBlank(message = "Nombre es obligatorio")
    String name,

    @NotNull(message = "Precio es obligatorio")
    @Positive(message = "Precio debe ser mayor que cero")
    BigDecimal price,
    Instant createdAt
) {}
