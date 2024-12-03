package com.fooddeliveryapp.RestaurantService.models;

import java.util.UUID;

import lombok.Data;

@Data
public class DishResponse {
	private UUID dishId;
    private String name;
    private Double price;
    private String description;
    private Boolean availability;
}
