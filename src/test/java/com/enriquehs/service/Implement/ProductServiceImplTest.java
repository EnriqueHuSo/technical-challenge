package com.enriquehs.service.Implement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enriquehs.dto.CreateProductRequest;
import com.enriquehs.dto.ProductDTO;
import com.enriquehs.error.NoContentException;
import com.enriquehs.error.ProductNotFoundException;
import com.enriquehs.mapper.ProductMapper;
import com.enriquehs.model.Product;
import com.enriquehs.repository.IProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private IProductRepository productRepo;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO dto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1500.00"));
        product.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));

        dto = new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }

    @Test
    void findAll_whenProductsExist_returnsDtos() {
        when(productRepo.findAll()).thenReturn(Flux.just(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        StepVerifier.create(productService.findAll())
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void findAll_whenNoProductsExist_throwsNoContentException() {
        when(productRepo.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(productService.findAll())
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(NoContentException.class)
                            .hasMessage("No se encontraron productos");
                })
                .verify();
    }

    @Test
    void findById_whenProductExists_returnsDto() {
        when(productRepo.findById(1L)).thenReturn(Mono.just(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        StepVerifier.create(productService.findById(1L))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void create_whenRequestIsValid_returnsCreatedDto() {
        CreateProductRequest request = new CreateProductRequest("Laptop", new BigDecimal("1500.00"));
        Product createdEntity = new Product();
        createdEntity.setName("Laptop");
        createdEntity.setPrice(new BigDecimal("1500.00"));
        createdEntity.setCreatedAt(Instant.now());

        when(productRepo.save(any(Product.class))).thenReturn(Mono.just(createdEntity));
        when(productMapper.toDto(createdEntity)).thenReturn(dto);

        StepVerifier.create(productService.create(request))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void findById_whenProductDoesNotExist_throwsProductNotFoundException() {
        when(productRepo.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(productService.findById(999L))
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ProductNotFoundException.class)
                            .hasMessage("Producto no encontrado con id: 999");
                })
                .verify();
    }

    @Test
    void updateDto_whenPriceIsNegative_shouldFailValidation() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ProductDTO invalidDto = new ProductDTO(2L, "Azúcar", new BigDecimal("-4.80"), Instant.now());

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(invalidDto);

        assertThat(violations)
                .isNotEmpty()
                .anySatisfy(violation -> assertThat(violation.getMessage()).isEqualTo("Precio debe ser mayor que cero"));
    }

    @Test
    void update_whenProductExists_updatesAndReturnsDto() {
        ProductDTO updatedDto = new ProductDTO(1L, "Laptop Pro", new BigDecimal("2100.00"), Instant.now());
        Product updatedEntity = new Product();
        updatedEntity.setId(1L);
        updatedEntity.setName("Laptop Pro");
        updatedEntity.setPrice(new BigDecimal("2100.00"));
        updatedEntity.setCreatedAt(product.getCreatedAt());

        when(productRepo.findById(1L)).thenReturn(Mono.just(product));
        when(productMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(productRepo.save(updatedEntity)).thenReturn(Mono.just(updatedEntity));
        when(productMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        StepVerifier.create(productService.update(1L, updatedDto))
                .expectNext(updatedDto)
                .verifyComplete();
    }

    @Test
    void update_whenProductDoesNotExist_throwsProductNotFoundException() {
        ProductDTO updatedDto = new ProductDTO(999L, "No product", new BigDecimal("10.00"), Instant.now());

        when(productRepo.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(productService.update(999L, updatedDto))
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ProductNotFoundException.class)
                            .hasMessage("Producto no encontrado con id: 999");
                })
                .verify();
    }

    @Test
    void delete_whenProductExists_deletesSuccessfully() {
        when(productRepo.findById(1L)).thenReturn(Mono.just(product));
        when(productRepo.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(productService.delete(1L))
                .verifyComplete();
    }

    @Test
    void delete_whenProductDoesNotExist_throwsProductNotFoundException() {
        when(productRepo.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(productService.delete(999L))
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(ProductNotFoundException.class)
                            .hasMessage("Producto no encontrado con id: 999");
                })
                .verify();
    }
}
