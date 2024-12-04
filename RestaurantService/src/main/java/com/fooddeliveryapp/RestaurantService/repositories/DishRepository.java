package com.fooddeliveryapp.RestaurantService.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddeliveryapp.RestaurantService.entities.Dish;

/**
 * Repository interface for managing the persistence of Dish entities.
 * This interface extends JpaRepository, which provides basic CRUD operations and additional
 * functionalities for managing Dish entities in the database.
 * 
 * The first type parameter is the entity class (Dish) and the second is the type of the entity's ID (UUID).
 */
public interface DishRepository extends JpaRepository<Dish, UUID> {
	 /**
     * Finds all dishes that belong to the specified restaurant.
     * 
     * @param restaurantId - The ID of the restaurant.
     * @return A list of dishes for the specified restaurant.
     */
	List<Dish> findByRestaurant_RestaurantId(UUID restaurantId);
}
