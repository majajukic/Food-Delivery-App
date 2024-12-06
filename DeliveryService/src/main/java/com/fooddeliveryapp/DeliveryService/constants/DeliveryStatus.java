package com.fooddeliveryapp.DeliveryService.constants;

/**
 * Represents the status of the delivery.
 * This enumerator is mapped to a column in the Order table in the database.
 */
public enum DeliveryStatus {
	IN_PROGRESS,
	DELIVERED,
	FAILED
}
