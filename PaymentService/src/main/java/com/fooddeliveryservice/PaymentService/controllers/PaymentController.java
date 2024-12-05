package com.fooddeliveryservice.PaymentService.controllers;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddeliveryservice.PaymentService.models.PaymentRequest;
import com.fooddeliveryservice.PaymentService.models.PaymentResponse;
import com.fooddeliveryservice.PaymentService.services.IPaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {
	@Autowired
	private IPaymentService paymentService;
	
	/**
	 * Retrieves the payment details for a specific order.
	 * This method fetches payment information associated with the provided order ID 
	 * and returns it as a PaymentResponse object.
	 * 
	 * @param orderId - The ID of the order whose payment details are to be retrieved.
	 * @return A ResponseEntity containing the payment details and an HTTP status of OK (200).
	 */
	@GetMapping("{orderId}")
	public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable UUID orderId) {
		PaymentResponse paymentResponse = paymentService.getPaymentDetailsByOrderId(orderId);
		
		return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
	}
	/**
	 * Processes a payment.
	 * This method receives a payment request object in the request body, 
	 * creates a new PaymentDetails entity and returns the payment ID.
	 * 
	 * @param paymentRequest - The data needed to create a new payment, including details abou the amount, payment method etc.
	 * @return A response containing the ID of the newly created payment and a CREATED status (201).
	 */
	@PostMapping
	public ResponseEntity<UUID> pay(@RequestBody @Valid PaymentRequest paymentRequest) {
		UUID paymentId = paymentService.createPayment(paymentRequest);
		
		return new ResponseEntity<>(paymentId, HttpStatus.CREATED);
	}
}
