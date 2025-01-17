package com.example.fooddeliveryapp.OrderService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

import reactor.core.publisher.Flux;

public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier{

	 @Override
	    public Flux<List<ServiceInstance>> get() {
	        List<ServiceInstance> result
	                = new ArrayList<>();
	        result.add(new DefaultServiceInstance(
	                "PAYMENT-SERVICE",
	                "PAYMENT-SERVICE",
	                "localhost",
	                8080,
	                false
	        ));

	        result.add(new DefaultServiceInstance(
	                "RESTAURANT-SERVICE",
	                "RESTAURANT-SERVICE",
	                "localhost",
	                8080,
	                false
	        ));
	        
	        result.add(new DefaultServiceInstance(
	                "DELIVERY-SERVICE",
	                "DELIVERY-SERVICE",
	                "localhost",
	                8080,
	                false
	        ));

	        return Flux.just(result);
	    }

	@Override
	public String getServiceId() {
		// TODO Auto-generated method stub
		return null;
	}
}
