package com.aceprogramming.productservice.service;

import com.aceprogramming.productservice.dto.ProductRequest;
import com.aceprogramming.productservice.dto.ProductResponse;
import com.aceprogramming.productservice.module.Product;
import com.aceprogramming.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        //products.stream().map(product -> maptoProductResponse(product))
        return products.stream().map(this::maptoProductResponse).toList();
    }

    private ProductResponse maptoProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

}
