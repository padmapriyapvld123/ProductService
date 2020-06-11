package com.nineleaps.product.repository;

import java.util.Optional;


import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

import com.nineleaps.product.entity.ProductEntity;
import com.nineleaps.product.entity.ProductTablePrimaryKey;
import com.nineleaps.product.model.Product;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity,String> {

	
}
