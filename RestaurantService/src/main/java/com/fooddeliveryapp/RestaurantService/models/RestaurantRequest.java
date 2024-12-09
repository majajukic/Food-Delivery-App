package com.fooddeliveryapp.RestaurantService.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Represents a request to create a new restaurant.
 * This model is used to transfer data from the client
 * to the server when creating a new restaurant record.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantRequest {
	@NotEmpty(message = "Restaurant name is required.")
    @Size(max = 100, message = "Restaurant name should not exceed 100 characters.")
    private String name;

    @NotEmpty(message = "Restaurant address is required.")
    @Size(max = 255, message = "Restaurant address should not exceed 255 characters.")
    private String address;

    @NotEmpty(message = "Restaurant phone number is required.")
    @Size(max = 15, message = "Restaurant phone number should not exceed 15 characters.")
    private String phoneNumber;
}
