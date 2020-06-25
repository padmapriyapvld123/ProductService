package com.nineleaps.product.controller;

import java.util.ArrayList;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nineleaps.product.entity.ProductEntity;
import com.nineleaps.product.entity.ProductTablePrimaryKey;
import com.nineleaps.product.exception.NoContentException;
import com.nineleaps.product.model.Product;
import com.nineleaps.product.model.Supplier;
import com.nineleaps.product.repository.ProductRepository;
import com.nineleaps.product.service.ProductService;
import com.nineleaps.product.proxy.SupplierProxy;

@RestController

@RequestMapping("/product")
public class ProductController {
	
	@Autowired(required=true)
	private SupplierProxy proxy;


	
	private ProductService productService;
	
	@Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

	@Autowired
	private ProductRepository productRepository;

	
	@PostMapping("/save")
	public ResponseEntity<?> saveIntoProductTable(@RequestBody Product product) {
		
		Supplier supplier = saveIfSupplierAvailable(product);

		if (supplier != null && supplier.getSupplierId() != null) {
			return new ResponseEntity<>(productService.saveIntoProductTable(product), HttpStatus.OK);
		} else {

			return new ResponseEntity<>("Supplier Serivce Feign Exception", HttpStatus.NO_CONTENT);
		}

	
		
	}

	@GetMapping(path = "{id}")
	public ResponseEntity<?> fetchRecordFromProductTable(@PathVariable("id") String productId) {
		Product productData = null;
		ResponseEntity<?> responseEntity;
		try {
			productData = productService.fetchRecordFromProductTable(productId);
			
			responseEntity =getIfSupplierAvailable(productData);
			
			return responseEntity;

			} catch (NoContentException e) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

	}

	private ResponseEntity<?> getIfSupplierAvailable(Product productData) {
			
		if (productData != null) {
			Supplier supplier = proxy.checkSupplierAvailability(productData.getSupplierId());
			if (supplier != null)
				return new ResponseEntity<>(productData, HttpStatus.OK);
			else

				return new ResponseEntity<>("Supplier is not available.", HttpStatus.NOT_FOUND);

		} else
			return new ResponseEntity<>("Product not found by id.", HttpStatus.NOT_FOUND);

		
	}
	
	private Supplier saveIfSupplierAvailable(Product product) {
		
		Supplier supplier = proxy.checkSupplierAvailability(product.getSupplierId());
		return supplier;
		
	}

	@PutMapping("/updateProduct/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable("id") String productId, @RequestBody Product product) {
		Optional<ProductEntity> productData = productRepository.findById(productId);

		if (productData.isPresent()) {

			return new ResponseEntity<>(productService.updateRecordIntoProductTable(product), HttpStatus.OK);

		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/deleteProduct/{id}")
	public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") String productId) {
		try {

			productRepository.deleteById(productId);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@GetMapping("/getAllProducts")
	public ResponseEntity<List<ProductEntity>> getAllProducts() {
		try {
			List<ProductEntity> products = new ArrayList<ProductEntity>();

			productRepository.findAll().forEach(products::add);

			if (products.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(products, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
