package com.enriquehs.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.enriquehs.dto.CreateProductRequest;
import com.enriquehs.dto.ProductDTO;
import com.enriquehs.service.IProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IProductService productService;

    @Test
    void getAllProducts_whenProductsExist_returnsOk() {
        ProductDTO product = new ProductDTO(1L, "Laptop", new BigDecimal("1500.00"), Instant.now());

        when(productService.findAll()).thenReturn(Flux.just(product));

        webTestClient.get()
                .uri("/api/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDTO.class)
                .hasSize(1);
    }

    @Test
    void getProductById_whenProductExists_returnsOk() {
        ProductDTO product = new ProductDTO(1L, "Laptop", new BigDecimal("1500.00"), Instant.now());

        when(productService.findById(1L)).thenReturn(Mono.just(product));

        webTestClient.get()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .value(dto -> dto.id().equals(1L));
    }

    @Test
    void createProduct_whenRequestIsValid_returnsCreated() {
        CreateProductRequest request = new CreateProductRequest("Teclado", new BigDecimal("40.00"));
        ProductDTO response = new ProductDTO(1L, "Teclado", new BigDecimal("40.00"), Instant.now());

        when(productService.create(any(CreateProductRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                      "name": "Teclado",
                      "price": 40.00
                    }
                    """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductDTO.class)
                .value(dto -> dto.name().equals("Teclado"));
    }

    @Test
    void createProduct_whenPriceIsNegative_returnsBadRequest() {
        webTestClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                      "name": "Teclado",
                      "price": -1
                    }
                    """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateProduct_whenRequestIsValid_returnsOk() {
        ProductDTO response = new ProductDTO(1L, "Teclado", new BigDecimal("50.00"), Instant.now());

        when(productService.update(anyLong(), any(ProductDTO.class))).thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                      "id": 1,
                      "name": "Teclado",
                      "price": 50.00,
                      "createdAt": "2026-09-18T06:59:21.900767Z"
                    }
                    """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .value(dto -> assertThat(dto.price()).isEqualByComparingTo(new BigDecimal("50.00")));
    }

    @Test
    void deleteProduct_whenProductExists_returnsNoContent() {
        when(productService.delete(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
