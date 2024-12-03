package com.fooddeliveryapp.RestaurantService.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Dish entity in the database.
 * This class is mapped to a table in the database where each instance
 * of this class corresponds to a dish record.
 */
@Entity
@Data // Generates getters, setters, toString(), equals(), and hashCode() methods automatically.
@AllArgsConstructor // Generates a constructor that accepts all fields as arguments.
@NoArgsConstructor // Generates a no-argument constructor, which is required by JPA.
@Builder // Implements the builder pattern, allowing for easy creation of Restaurant instances with specified values.
public class Dish {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "dish_id")
    private UUID dishId;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean availability;
}
