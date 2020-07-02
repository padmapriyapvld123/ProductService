package com.nineleaps.product.proxy;



import org.springframework.cloud.netflix.ribbon.RibbonClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nineleaps.product.model.Supplier;



@FeignClient(name="nineleaps-apigateway") // it'll worke with zuul gateway
//@FeignClient(name="nineleaps-registry", url="localhost:1020")
@RibbonClient(name="supplier")
public interface SupplierProxy {
	
	@GetMapping("/supplier/supplier/supplier/{id}")
	public Supplier checkSupplierAvailability( @PathVariable("id") String string);

}

