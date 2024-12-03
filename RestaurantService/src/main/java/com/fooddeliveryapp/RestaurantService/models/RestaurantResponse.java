package com.fooddeliveryapp.RestaurantService.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a response model for a restaurant.
 * This model is used to transfer data from the server
 * to the client when retrieving restaurant record(s).
 */
@Data
@Builder
@AllArgsConstructor 
@NoArgsConstructor
public class RestaurantResponse {
	private UUID restaurantId;
	private String name;
	private String address;
    private String phoneNumber;
}
