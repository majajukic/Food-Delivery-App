package com.example.fooddeliveryapp.OrderService.external.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.fooddeliveryapp.OrderService.external.models.DishResponse;


/**
 * Feign client interface for interacting with the Restaurant Service.
 * This interface is used to define communication with the Restaurant Service's REST endpoints.
 * It simplifies the process of calling external microservices by using declarative REST client functionality provided by Feign.
 */
@FeignClient(name = "RESTAURANT-SERVICE/restaurants")
public interface IRestaurantService {
	@GetMapping("/dishes/{dishId}")
    ResponseEntity<DishResponse> getDishById(@PathVariable UUID dishId);
}
