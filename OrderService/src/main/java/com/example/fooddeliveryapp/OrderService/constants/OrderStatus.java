package com.example.fooddeliveryapp.OrderService.constants;

/**
 * Represents the status of the order.
 * This enumerator is mapped to a column in the Order table in the database.
 */
public enum OrderStatus {
	PLACED,
	DELIVERING,
	DELIVERED,
	CANCELED
}
