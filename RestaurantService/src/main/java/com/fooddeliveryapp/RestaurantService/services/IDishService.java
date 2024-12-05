package com.fooddeliveryapp.RestaurantService.services;

import java.util.List;
import java.util.UUID;

import com.fooddeliveryapp.RestaurantService.models.DishRequest;
import com.fooddeliveryapp.RestaurantService.models.DishResponse;

import jakarta.validation.Valid;

/**
 * Interface for the Dish service that defines the operations 
 * related to dish management. This interface acts as a contract 
 * for the implementation of dish-related business logic.
 */
public interface IDishService {
	UUID addDishToRestaurant(UUID restaurantId, @Valid DishRequest dishRequest);

	List<DishResponse> getAllDishesForRestaurant(UUID restaurantId);

	DishResponse updateDish(UUID restaurantId, UUID dishId, @Valid DishRequest dishRequest);

	DishResponse getDishById(UUID dishId);
}
