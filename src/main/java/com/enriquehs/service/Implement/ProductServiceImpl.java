package com.enriquehs.service.Implement;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.enriquehs.dto.CreateProductRequest;
import com.enriquehs.dto.ProductDTO;
import com.enriquehs.error.NoContentException;
import com.enriquehs.error.ProductNotFoundException;
import com.enriquehs.mapper.ProductMapper;
import com.enriquehs.model.Product;
import com.enriquehs.repository.IProductRepository;
import com.enriquehs.service.IProductService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepo;
    private final ProductMapper productMapper;

    @Override
    public Flux<ProductDTO> findAll() {
        return productRepo.findAll()
                .switchIfEmpty(Flux.error(new NoContentException("No se encontraron productos")))
                .map(productMapper::toDto);
    }

    @Override
    public Mono<ProductDTO> findById(Long id) {
        return productRepo.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Producto no encontrado con id: " + id)))
                .map(productMapper::toDto);
    }

    @Override
    public Mono<ProductDTO> create(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setCreatedAt(Instant.now());

        return Mono.just(product)
                .flatMap(productRepo::save)
                .map(productMapper::toDto);
    }

    @Override
    public Mono<ProductDTO> update(Long id, ProductDTO dto) {
        return productRepo.findById(id)
            .switchIfEmpty(Mono.error(new ProductNotFoundException("Producto no encontrado con id: " + id)))
            .flatMap(existing ->
                    Mono.just(dto)
                            .map(productMapper::toEntity)
                            .map(updated -> {
                                updated.setId(existing.getId());
                                return updated;
                            })
                            .flatMap(productRepo::save)
            )
            .map(productMapper::toDto);

    }

    @Override
    public Mono<Void> delete(Long id) {
        return productRepo.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Producto no encontrado con id: " + id)))
                .flatMap(existing -> productRepo.deleteById(existing.getId()));
    }
}
