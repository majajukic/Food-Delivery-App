package com.fooddeliveryapp.RestaurantService.services;

import java.util.List;
import java.util.UUID;

import com.fooddeliveryapp.RestaurantService.models.RestaurantRequest;
import com.fooddeliveryapp.RestaurantService.models.RestaurantResponse;

import jakarta.validation.Valid;

/**
 * Interface for the Restaurant service that defines the operations 
 * related to restaurant management. This interface acts as a contract 
 * for the implementation of restaurant-related business logic.
 */
public interface IRestaurantService {
	List<RestaurantResponse> getAllRestaurants();
	
	RestaurantResponse getRestaurantById(UUID restaurantId);
	
    UUID addRestaurant(@Valid RestaurantRequest restaurantRequest);

	RestaurantResponse updateRestaurant(UUID restaurantId, @Valid RestaurantRequest restaurantRequest);

	void deleteRestaurant(UUID restaurantId);
}
