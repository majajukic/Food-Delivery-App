package com.example.fooddeliveryapp.OrderService.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fooddeliveryapp.OrderService.entities.Order;

/**
 * Repository interface for managing the persistence of Order entities.
 * This interface extends JpaRepository, which provides basic CRUD operations and additional
 * functionalities for managing Order entities in the database.
 * 
 * The first type parameter is the entity class (Order) and the second is the type of the entity's ID (UUID).
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

}
