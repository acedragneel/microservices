package com.aceprogramming.productservice;

import com.aceprogramming.productservice.controller.ProductController;
import com.aceprogramming.productservice.dto.ProductRequest;
import com.aceprogramming.productservice.dto.ProductResponse;
import com.aceprogramming.productservice.module.Product;
import com.aceprogramming.productservice.repository.ProductRepository;
import com.github.dockerjava.api.model.ExternalCA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private org.springframework.http.converter.json.Jackson2ObjectMapperBuilder objectMapperBuilder;

	@Autowired
	private ProductRepository productRepository;

	@InjectMocks
	private ProductController productController;


	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {

		ProductRequest productRequest = getProductRequest();
		String productRequestStr = objectMapperBuilder.build().writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestStr))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		// Mocking data
		Product product1 = Product.builder()
				.name("1")
				.description("Product 1")
				.price(BigDecimal.valueOf(10.0))
				.build();
		Product product2 = Product.builder()
				.name("1")
				.description("Product 2")
				.price(BigDecimal.valueOf(20.0))
				.build();
		List<Product> productList = Arrays.asList(product1, product2);

		// Mocking repository behavior
		when(productRepository.findAll()).thenReturn(productList);

		// Performing GET request
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(productList.size()))
				.andExpect(jsonPath("$[0].id").value(product1.getId()))
				.andExpect(jsonPath("$[0].name").value(product1.getName()))
				.andExpect(jsonPath("$[0].price").value(product1.getPrice()))
				.andExpect(jsonPath("$[1].id").value(product2.getId()))
				.andExpect(jsonPath("$[1].name").value(product2.getName()))
				.andExpect(jsonPath("$[1].price").value(product2.getPrice()));

		// Verifying repository method invocation
		verify(productRepository, times(1)).findAll();
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iphone 13")
				.description("iphone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

	private List<ProductResponse> getProductResponse() {
		List<Product> products = productRepository.findAll();
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
