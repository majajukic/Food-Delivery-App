package com.example.fooddeliveryapp.OrderService.external.models;

import java.util.UUID;

import lombok.Data;

/**
 * Represents a response model for a dish.
 * This model is used to transfer data from the server
 * to the client when retrieving dish record(s).
 */
@Data
public class DishResponse {
	private UUID dishId;
    private String name;
    private Double price;
    private String description;
    private Boolean availability;
}

