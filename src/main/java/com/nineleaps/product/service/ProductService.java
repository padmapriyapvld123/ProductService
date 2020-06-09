package com.nineleaps.product.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import com.nineleaps.product.entity.ProductEntity;
import com.nineleaps.product.entity.ProductTablePrimaryKey;
import com.nineleaps.product.exception.NoContentException;
import com.nineleaps.product.model.Product;
import com.nineleaps.product.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public Product saveIntoProductTable(Product product) {
		ProductEntity entity = productRepository.save(mapObjectToEntity(product));
		return mapEntityToObject(entity);
	}

	public ProductEntity mapObjectToEntity(Product product) {
		ProductTablePrimaryKey primaryKey = new ProductTablePrimaryKey();
		primaryKey.setProductId(product.getProductId());
	
		ProductEntity entity = new ProductEntity();
		entity.setProductId(primaryKey.getProductId());
		entity.setDescription(product.getDescription());
		entity.setName(product.getName());
		entity.setPrice(product.getPrice());
		entity.setSupplierId(product.getSupplierId());
		
		return entity;
	}

	
	public Product mapEntityToObject(ProductEntity entity) {
		Product table = new Product();
		ProductTablePrimaryKey primaryKey = new ProductTablePrimaryKey();
		primaryKey.setProductId(entity.getProductId());
	
		table.setProductId(primaryKey.getProductId());
		table.setName(entity.getName());
		table.setPrice(entity.getPrice());
		table.setDescription(entity.getDescription());
		table.setSupplierId(entity.getSupplierId());
		return table;
	}
	
	public Product fetchRecordFromProductTable(String id) throws NoContentException {
		ProductTablePrimaryKey productTablePrimaryKey= new ProductTablePrimaryKey();
		productTablePrimaryKey.setProductId(id);
		Optional<ProductEntity> entity = productRepository.findById(productTablePrimaryKey);
		if(!entity.isPresent()) {
			throw new NoContentException(HttpStatus.NO_CONTENT);
		}
		return mapEntityToObject(entity.get());
	
}

	public Product updateRecordIntoProductTable(Product product) {
		ProductEntity entity = productRepository.save(mapObjectToEntity(product));
		  return mapEntityToObject(entity);
		 
}
}