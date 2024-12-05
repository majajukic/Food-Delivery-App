package com.fooddeliveryservice.PaymentService.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fooddeliveryservice.PaymentService.entities.PaymentDetails;

/**
 * Repository interface for managing the persistence of Payment entities.
 * This interface extends JpaRepository, which provides basic CRUD operations and additional
 * functionalities for managing Payment entities in the database.
 * 
 * The first type parameter is the entity class (Payment) and the second is the type of the entity's ID (UUID).
 */
@Repository
public interface PaymentRepository extends JpaRepository<PaymentDetails, UUID>{
	/**
	 * Retrieves a PaymentDetails entity by the associated order ID.
	 * This method is used to find the payment information that corresponds to a specific order.
	 * It returns an Optional containing the PaymentDetails if found, or an empty Optional if no payment is associated with the provided order ID.
	 * 
	 * @param orderId - The ID of the order for which the payment details are to be retrieved.
	 * @return An Optional containing the PaymentDetails for the given orderId, or an empty Optional if no payment is found.
	 */
	Optional<PaymentDetails> findByOrderId(UUID orderId);
}
