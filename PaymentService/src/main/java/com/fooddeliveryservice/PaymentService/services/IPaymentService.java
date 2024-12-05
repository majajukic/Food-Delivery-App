package com.fooddeliveryservice.PaymentService.services;

import java.util.UUID;

import com.fooddeliveryservice.PaymentService.models.PaymentRequest;

import jakarta.validation.Valid;

/**
 * Interface for the Payment service that defines the operations 
 * related to payment management. This interface acts as a contract 
 * for the implementation of payment-related business logic.
 */
public interface IPaymentService {

	UUID createPayment(@Valid PaymentRequest paymentRequest);
}
