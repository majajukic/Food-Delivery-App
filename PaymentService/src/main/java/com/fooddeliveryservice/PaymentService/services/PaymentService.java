package com.fooddeliveryservice.PaymentService.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fooddeliveryservice.PaymentService.constants.PaymentStatus;
import com.fooddeliveryservice.PaymentService.entities.PaymentDetails;
import com.fooddeliveryservice.PaymentService.exceptions.PaymentNotFoundException;
import com.fooddeliveryservice.PaymentService.models.PaymentRequest;
import com.fooddeliveryservice.PaymentService.models.PaymentResponse;
import com.fooddeliveryservice.PaymentService.repositories.PaymentRepository;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PaymentService implements IPaymentService {
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	/**
	 * Retrieves the payment details for a specific order.
	 * This method fetches payment information associated with the provided order ID 
	 * and returns it as a PaymentResponse object.
	 * 
	 * @param orderId - The ID of the order whose payment details are to be retrieved.
	 * @return A PaymentResponse object containing the payment details.
	 * @throws PaymentNotFoundException if no payment is found for the given order ID.
	 */
	@Override
	public PaymentResponse getPaymentDetailsByOrderId(UUID orderId) {
		log.info("Retrieving details for payment with an order ID of {}...", orderId);
		
		PaymentDetails paymentDetails = paymentRepository.findByOrderId(orderId)
		        .orElseThrow(() -> {
		            log.error("Payment with order ID of {} not found", orderId);
		            return new PaymentNotFoundException("Payment with an order ID of " + orderId + " not found");
		        });

		
		PaymentResponse paymentResponse = PaymentResponse.builder()
				.paymentId(paymentDetails.getPaymentId())
				.paymentMode(paymentDetails.getPaymentMode())
				.status(paymentDetails.getStatus())
				.payedOn(paymentDetails.getTimestamp())
				.build();
		
		log.info("Payment details for the payment with an order ID of {} retireved successfully.", orderId);
		
		return paymentResponse;
	}
	
	/**
	 * Processes a payment and saves the payment details in the database.
	 * 
	 * This method receives a PaymentRequest object, which contains the order ID, reference number,
	 * payment amount, and payment mode. It then creates a PaymentDetails object with the provided information
	 * and additional data such as the current timestamp and the payment status.
	 * After saving the payment details in the repository, it logs a success message and returns the payment ID.
	 * 
	 * @param paymentRequest - The request containing payment details such as order ID, reference number, amount,
	 *                         and payment mode.
	 * @return The ID of the successfully created payment.
	 */
	@Override
	public UUID createPayment(@Valid PaymentRequest paymentRequest) {
		log.info("Procesing payment...");
		
		PaymentDetails paymentDetails = PaymentDetails.builder()
				.orderId(paymentRequest.getOrderId())
				.amount(paymentRequest.getAmount())
				.paymentMode(paymentRequest.getPaymentMode())
				.timestamp(LocalDateTime.now())
				.status(PaymentStatus.SUCCESSFUL)
				.build();
		
		paymentRepository.save(paymentDetails);
		
		log.info("Payment processed successfully.");
		
		return paymentDetails.getPaymentId();
	}
}
