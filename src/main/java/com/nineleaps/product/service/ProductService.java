package com.nineleaps.product.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nineleaps.product.entity.ProductEntity;
import com.nineleaps.product.entity.ProductTablePrimaryKey;
import com.nineleaps.product.exception.NoContentException;
import com.nineleaps.product.model.Product;
import com.nineleaps.product.model.Supplier;
import com.nineleaps.product.proxy.SupplierProxy;
import com.nineleaps.product.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired(required = true)
	private SupplierProxy proxy;

	@Autowired
	private ProductRepository productRepository;

	public Product saveIntoProductTable(Product product) {
		ProductEntity entity = productRepository.save(mapObjectToEntity(product));
		return mapEntityToObject(entity);
	}

	public ProductEntity mapObjectToEntity(Product product) {

		ProductEntity entity = new ProductEntity();
		entity.setProductId(product.getProductId());
		entity.setDescription(product.getDescription());
		entity.setName(product.getName());
		entity.setPrice(product.getPrice());
		entity.setSupplierId(product.getSupplierId());

		return entity;
	}

	public Product mapEntityToObject(ProductEntity entity) {
		Product table = new Product();

		table.setProductId(entity.getProductId());
		table.setName(entity.getName());
		table.setPrice(entity.getPrice());
		table.setDescription(entity.getDescription());
		table.setSupplierId(entity.getSupplierId());
		return table;
	}

	public Product fetchRecordFromProductTable(String id) throws NoContentException {
		Optional<ProductEntity> entity = productRepository.findById(id);
		if (!entity.isPresent()) {
			throw new NoContentException(HttpStatus.NO_CONTENT);
		}
		return mapEntityToObject(entity.get());

	}

	public Product updateRecordIntoProductTable(Product product) {
		ProductEntity entity = productRepository.save(mapObjectToEntity(product));
		return mapEntityToObject(entity);

	}

	public Supplier saveIfSupplierAvailable(Product product) {

		Supplier supplier = proxy.checkSupplierAvailability(product.getSupplierId());
		return supplier;

	}

	public ResponseEntity<?> getIfSupplierAvailable(Product productData) {

		if (productData != null) {
			Supplier supplier = proxy.checkSupplierAvailability(productData.getSupplierId());
			if (supplier != null)
				return new ResponseEntity<>(productData, HttpStatus.OK);
			else

				return new ResponseEntity<>("Supplier is not available.", HttpStatus.NOT_FOUND);

		} else
			return new ResponseEntity<>("Product not found by id.", HttpStatus.NOT_FOUND);

	}

}