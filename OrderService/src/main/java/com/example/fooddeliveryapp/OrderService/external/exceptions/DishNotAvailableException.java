package com.example.fooddeliveryapp.OrderService.external.exceptions;

public class DishNotAvailableException extends IllegalStateException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DishNotAvailableException(String message) {
        super(message);
    }
}
