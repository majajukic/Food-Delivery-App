package com.fooddeliveryapp.RestaurantService.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fooddeliveryapp.RestaurantService.entities.Restaurant;

/**
 * Repository interface for managing the persistence of Restaurant entities.
 * This interface extends JpaRepository, which provides basic CRUD operations and additional
 * functionalities for managing Restaurant entities in the database.
 * 
 * The first type parameter is the entity class (Restaurant) and the second is the type of the entity's ID (Long).
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID>{

}
