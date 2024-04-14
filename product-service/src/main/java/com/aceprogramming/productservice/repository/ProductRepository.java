package com.aceprogramming.productservice.repository;

import com.aceprogramming.productservice.module.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {

}
