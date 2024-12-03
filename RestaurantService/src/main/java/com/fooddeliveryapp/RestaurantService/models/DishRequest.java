package com.fooddeliveryapp.RestaurantService.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

/**
 * Represents a request to add a new dish to a restaurant.
 * This model is used to transfer data from the client to the server
 * when creating a new dish record.
 */
@Data
public class DishRequest { 
    @NotEmpty(message = "Dish name is required.")
    @Size(max = 100, message = "Dish name should not exceed 100 characters.")
    private String name;

    @NotNull(message = "Dish price is required.")
    private Double price;

    @Size(max = 255, message = "Dish description should not exceed 255 characters.")
    private String description;

    @NotNull(message = "Dish availability is required.")
    private Boolean availability;
}