package com.fooddeliveryapp.DeliveryService.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fooddeliveryapp.DeliveryService.entities.DeliveryDetails;

/**
 * Repository interface for managing the persistence of Delivery entities.
 * This interface extends JpaRepository, which provides basic CRUD operations and additional
 * functionalities for managing Delivery entities in the database.
 * 
 * The first type parameter is the entity class (Delivery) and the second is the type of the entity's ID (UUID).
 */
@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryDetails, UUID> {
	/**
     * Custom JPA query method to find a delivery by its associated order ID.
     * This method returns an Optional of DeliveryDetails to handle the possibility that 
     * the delivery with the provided order ID may not exist in the database.
     * 
     * @param orderId The UUID of the order associated with the delivery.
     * @return Optional<DeliveryDetails> containing the delivery details if found, or an empty Optional if not found.
     */
	Optional<DeliveryDetails> findByOrderId(UUID orderId);
}
