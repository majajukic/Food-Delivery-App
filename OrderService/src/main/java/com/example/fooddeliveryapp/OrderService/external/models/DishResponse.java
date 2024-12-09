package com.example.fooddeliveryapp.OrderService.external.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a response model for a dish.
 * This model is used to transfer data from the server
 * to the client when retrieving dish record(s).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishResponse {
	private UUID dishId;
    private String name;
    private Double price;
    private String description;
    private Boolean availability;
}

