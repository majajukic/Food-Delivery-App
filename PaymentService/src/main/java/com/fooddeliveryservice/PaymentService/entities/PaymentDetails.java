package com.fooddeliveryservice.PaymentService.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fooddeliveryservice.PaymentService.constants.PaymentMode;
import com.fooddeliveryservice.PaymentService.constants.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents an PaymentDetails entity in the database.
 * This class is mapped to a table in the database where each instance
 * of this class corresponds to an payment record.
 */
@Entity
@Data // Generates getters, setters, toString(), equals(), and hashCode() methods automatically.
@AllArgsConstructor // Generates a constructor that accepts all fields as arguments.
@NoArgsConstructor // Generates a no-argument constructor, which is required by JPA.
@Builder 
public class PaymentDetails {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id")
	private UUID paymentId;
	
	@Column(name = "order_id", nullable = false)
	private UUID orderId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_mode", nullable = false)
	private PaymentMode paymentMode;
	
	@Column(name = "reference_number", nullable = false, length = 50)
	private String referenceNumber;
	
	@Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PaymentStatus status;
	
	@Column(name = "amount", nullable = false)
	private Double amount;
}
