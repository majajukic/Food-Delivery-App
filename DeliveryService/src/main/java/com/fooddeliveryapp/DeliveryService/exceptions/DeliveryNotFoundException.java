package com.fooddeliveryapp.DeliveryService.exceptions;

public class DeliveryNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DeliveryNotFoundException(String message) {
		super(message);
	}
}
