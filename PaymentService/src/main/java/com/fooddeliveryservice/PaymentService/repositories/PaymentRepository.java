package com.fooddeliveryservice.PaymentService.repositories;

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

}
