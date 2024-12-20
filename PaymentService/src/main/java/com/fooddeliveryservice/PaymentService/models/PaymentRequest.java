package com.fooddeliveryservice.PaymentService.models;

import java.util.UUID;

import com.fooddeliveryservice.PaymentService.constants.PaymentMode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a request to create a new payment.
 * This model is used to transfer data from the client to the server
 * when creating a new payment record.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
	@NotNull(message = "Order ID cannot be null.")
	private UUID orderId;
	
    @NotNull(message = "Amount cannot be null.")
    @Positive(message = "Amount must be greater than zero.")
	private Double amount;
	
	@NotNull(message = "Payment mode cannot be null.")
	private PaymentMode paymentMode;
}
