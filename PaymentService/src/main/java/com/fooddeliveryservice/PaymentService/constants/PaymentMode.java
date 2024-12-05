package com.fooddeliveryservice.PaymentService.constants;

/**
 * Represents the mode of the payment.
 * This enumerator is mapped to a column in the Payment table in the database.
 */
public enum PaymentMode {
	CASH,
	CARD,
	PAYPAL
}