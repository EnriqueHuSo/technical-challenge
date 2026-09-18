package com.enriquehs.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.enriquehs.model.Product;

public interface IProductRepository extends ReactiveCrudRepository<Product, Long> {

}
