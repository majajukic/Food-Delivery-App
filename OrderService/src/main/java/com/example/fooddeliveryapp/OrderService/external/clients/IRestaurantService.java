package com.example.fooddeliveryapp.OrderService.external.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.fooddeliveryapp.OrderService.external.fallbacks.RestaurantServiceFallback;
import com.example.fooddeliveryapp.OrderService.external.models.DishResponse;


/**
 * Feign client interface for interacting with the Restaurant Service.
 * This interface is used to define communication with the Restaurant Service's REST endpoints.
 * It simplifies the process of calling external microservices by using declarative REST client functionality provided by Feign.
 * 
 * The fallback method is implemented to handle service unavailability scenarios. 
 * If the Restaurant Service is unavailable or an error occurs during the request, 
 * the fallback method will be triggered, providing a default behavior or response.
 */
@FeignClient(name = "RESTAURANT-SERVICE/restaurants", fallback = RestaurantServiceFallback.class)
public interface IRestaurantService {
	@GetMapping("/dishes/{dishId}")
    ResponseEntity<DishResponse> getDishById(@PathVariable UUID dishId);
}
