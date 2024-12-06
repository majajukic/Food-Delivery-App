package com.example.fooddeliveryapp.OrderService.external.fallbacks;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.fooddeliveryapp.OrderService.external.clients.IRestaurantService;
import com.example.fooddeliveryapp.OrderService.external.models.DishResponse;

import lombok.extern.log4j.Log4j2;

/**
 * Fallback implementation of the IRestaurantService interface.
 * This class is invoked when the Restaurant Service is unavailable or encounters an error
 * while interacting with the Restaurant Service via Feign.
 */
@Component
@Log4j2
public class RestaurantServiceFallback implements IRestaurantService {

    /**
     * Fallback method for getDishById.
     * This method is triggered when the Restaurant Service cannot be reached or encounters an error.
     * It logs the error and returns an HTTP 503 status with no body.
     * 
     * @param dishId The ID of the dish requested.
     * @return A ResponseEntity with a 503 status and no body, indicating service unavailability.
     */
	@Override
	public ResponseEntity<DishResponse> getDishById(UUID dishId) {
		log.error("Fallback triggered for getDishById");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
	}
}
