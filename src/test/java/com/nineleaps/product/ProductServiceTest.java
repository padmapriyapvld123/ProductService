package com.nineleaps.product;

import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnit;
import org.cassandraunit.spring.CassandraUnitDependencyInjectionTestExecutionListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest({ "spring.data.cassandra.port=9042", "spring.data.cassandra.keyspace-name=cycling1" })
@EnableAutoConfiguration
@ComponentScan
@ContextConfiguration
//@TestExecutionListeners({ CassandraUnitDependencyInjectionTestExecutionListener.class,
//    DependencyInjectionTestExecutionListener.class })
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

	@Before
	public void init() {
		productController.setProductService(productService);

		this.mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
		// mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

	}

	@Test
	public void getProductById() throws Exception {
		Product productResult = null;
		
		Product product = new Product("P001", "Table", 100.0, "Black Table", "test");
		
	
		mockProductService = Mockito.mock(ProductService.class);

		when(mockProductService.getIfSupplierAvailable(product)).thenReturn(new ResponseEntity(product, HttpStatus.OK));

		productResult = productService.fetchRecordFromProductTable(product.getProductId());

		assertEquals(productResult.getSupplierId(), product.getSupplierId());
	}

	@Test
	public void givenAllProducts() throws Exception {

		ProductEntity product = new ProductEntity("P001", "Table", 100.0, "Black Table", "test");

		mockMvc.perform(get("/product/getAllProducts").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value(product.getName()));
	}

	@Test
	public void saveProduct() throws Exception {

		Product productResult = null;
		
		Supplier supplier = new Supplier("test","test","test");

		Product product = new Product("P001", "Table", 100.0, "Black Table", "test");

		mockProductService = Mockito.mock(ProductService.class);

		when(mockProductService.saveIfSupplierAvailable(product)).thenReturn(supplier);

		productResult = productService.saveIntoProductTable(product);

		assertEquals(productResult.getSupplierId(), product.getSupplierId());
	}

}