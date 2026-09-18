package com.enriquehs.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PRODUCTOS")
public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;
    private Instant createdAt;
}
