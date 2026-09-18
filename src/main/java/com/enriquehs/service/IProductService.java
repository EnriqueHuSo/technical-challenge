package com.enriquehs.service;

import com.enriquehs.dto.CreateProductRequest;
import com.enriquehs.dto.ProductDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {
    Flux<ProductDTO> findAll();
    Mono<ProductDTO> findById(Long id);
    Mono<ProductDTO> create(CreateProductRequest request);
    Mono<ProductDTO> update(Long id, ProductDTO dto);
    Mono<Void> delete(Long id);
}
