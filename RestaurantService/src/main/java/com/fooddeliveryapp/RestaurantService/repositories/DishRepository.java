package com.fooddeliveryapp.RestaurantService.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddeliveryapp.RestaurantService.entities.Dish;

public interface DishRepository extends JpaRepository<Dish, UUID> {
	 /**
     * Finds all dishes that belong to the specified restaurant.
     * 
     * @param restaurantId - The ID of the restaurant.
     * @return A list of dishes for the specified restaurant.
     */
	List<Dish> findByRestaurant_RestaurantId(UUID restaurantId);
}
