package com.nineleaps.product.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;


@Table("product_supplier")

public class ProductEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@PrimaryKey private String productId;
	@Column("name")
	private String name;
	@Column("price")
	private double price;
	@Column("description")
	private String description;
	@Column("supplierid")
	private String supplierId;
	
	public ProductEntity() {
		
	}
	

	public ProductEntity(String productId, String name, double price, String description, String supplierId) {
		super();
		this.productId = productId;
		this.name = name;
		this.price = price;
		this.description = description;
		this.supplierId = supplierId;
	}


	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
		
	
		
}