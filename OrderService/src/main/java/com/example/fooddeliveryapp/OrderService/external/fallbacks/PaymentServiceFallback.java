package com.example.fooddeliveryapp.OrderService.external.fallbacks;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.fooddeliveryapp.OrderService.external.clients.IPaymentService;
import com.example.fooddeliveryapp.OrderService.external.models.PaymentRequest;
import com.example.fooddeliveryapp.OrderService.external.models.PaymentResponse;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

/**
 * Fallback implementation of the IPaymentService interface.
 * This class is invoked when the Payment Service is unavailable or encounters an error
 * while interacting with the Payment Service via Feign.
 */
@Component
@Log4j2
public class PaymentServiceFallback implements IPaymentService {
	
	/**
     * Fallback method for pay.
     * This method is triggered when the Payment Service cannot be reached or encounters an error.
     * It logs the error and returns an HTTP 503 status with no body.
     * 
     * @param paymentRequest The payment request details for the transaction.
     * @return A ResponseEntity with a 503 status and no body, indicating service unavailability.
     */
	@Override
	public ResponseEntity<UUID> pay(@Valid PaymentRequest paymentRequest) {
		log.error("Fallback triggered for pay");
	
	    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
	}

	 /**
     * Fallback method for getPaymentDetailsByOrderId.
     * This method is triggered when the Payment Service cannot be reached or encounters an error.
     * It logs the error and returns an HTTP 503 status with no body.
     * 
     * @param orderId The ID of the order for which payment details are requested.
     * @return A ResponseEntity with a 503 status and no body, indicating service unavailability.
     */
	@Override
	public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(UUID orderId) {
		log.error("Fallback triggered for getPaymentDetailsByOrderId");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
	}
}
