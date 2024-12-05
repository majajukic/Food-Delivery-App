package com.example.fooddeliveryapp.OrderService.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
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
	@Modifying
	@Transactional
	@Query("UPDATE Order o SET o.status = :newStatus WHERE o.orderId = :orderId")
	void updateOrderStatus(@Param("orderId") UUID orderId, @Param("newStatus") OrderStatus newStatus);
}
