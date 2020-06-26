package com.nineleaps.product;

import org.cassandraunit.spring.CassandraDataSet;

import org.cassandraunit.spring.CassandraUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nineleaps.product.controller.ProductController;
import com.nineleaps.product.entity.ProductEntity;
import com.nineleaps.product.model.Product;
import com.nineleaps.product.model.Supplier;
import com.nineleaps.product.repository.ProductRepository;
import com.nineleaps.product.service.ProductService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(JUnitPlatform.class)
@SpringBootTest({ "spring.data.cassandra.port=9042", "spring.data.cassandra.keyspace-name=cycling1" })
@EnableAutoConfiguration
@ComponentScan
@ContextConfiguration
@CassandraDataSet(value = { "cassandra-init.sh" }, keyspace = "cycling1")
@CassandraUnit
public class ProductServiceTest {

	private MockMvc mockMvc;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ProductController productController;

	private ProductService mockProductService = null;

	@BeforeEach
	public void init() {
		productController.setProductService(productService);

		this.mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
		// mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

	}

	@Test
	@DisplayName("saveProduct")
	public void saveProduct() throws Exception {

		Product productResult = null;

		Supplier supplier = new Supplier("test", "test", "test");

		Product product = new Product("P001", "Table", 100.0, "Black Table", "test");

		mockProductService = Mockito.mock(ProductService.class);

		when(mockProductService.saveIfSupplierAvailable(product)).thenReturn(supplier);

		productResult = productService.saveIntoProductTable(product);

		assertEquals(productResult.getSupplierId(), product.getSupplierId());
	}

	@Test
	@DisplayName("getProductById")
	public void getProductById() throws Exception {
		Product productResult = null;

		Product product = new Product("P001", "Table", 100.0, "Black Table", "test");

		mockProductService = Mockito.mock(ProductService.class);

		when(mockProductService.getIfSupplierAvailable(product)).thenReturn(new ResponseEntity(product, HttpStatus.OK));

		productResult = productService.fetchRecordFromProductTable(product.getProductId());

		assertEquals(productResult.getSupplierId(), product.getSupplierId());
	}

	@Test
	@DisplayName("getAllProducts")
	public void getAllProducts() throws Exception {

		ProductEntity product = new ProductEntity("P001", "Table", 100.0, "Black Table", "test");

		mockMvc.perform(get("/product/getAllProducts").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value(product.getName()));
	}
	
	@Test
	public void updateProductAPI() throws Exception {

		Product productResult = null;

		Supplier supplier = new Supplier("test", "test", "test");

		Product product = new Product("P001", "Table", 100.0, "Black Table", "test");

		mockProductService = Mockito.mock(ProductService.class);

		when(mockProductService.saveIfSupplierAvailable(product)).thenReturn(supplier);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/product/updateProduct/{id}", "P001").content(asJsonString(product))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(product.getDescription()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(product.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.price").value(product.getPrice()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.supplierId").value(product.getSupplierId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(product.getProductId()));
		

		
	
	
	}



	@Test
	public void deleteSupplierAPI() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/product/deleteProduct/{id}", "P001"))
				.andExpect(status().isNoContent());
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}