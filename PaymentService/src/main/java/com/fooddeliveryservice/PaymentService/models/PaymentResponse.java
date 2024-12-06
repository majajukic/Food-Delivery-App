package com.fooddeliveryservice.PaymentService.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fooddeliveryservice.PaymentService.constants.PaymentMode;
import com.fooddeliveryservice.PaymentService.constants.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a response model for a payment.
 * This model is used to transfer data from the server
 * to the client when retrieving payment record(s).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
	private UUID paymentId;
	private PaymentMode paymentMode;
	private PaymentStatus status;
	private LocalDateTime payedOn;
}
