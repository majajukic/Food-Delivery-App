package com.example.fooddeliveryapp.OrderService.exceptions;

public class EmptyOrderException extends IllegalArgumentException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EmptyOrderException(String message) {
		super(message);
	}
}
