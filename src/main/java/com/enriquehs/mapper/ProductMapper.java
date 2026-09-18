package com.enriquehs.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.enriquehs.dto.ProductDTO;
import com.enriquehs.model.Product;

@Component
public class ProductMapper {

    public ProductDTO toDto(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }

    public Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.id());
        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setCreatedAt(dto.createdAt() != null ? dto.createdAt() : Instant.now());
        return product;
    }
}
