package com.fooddeliveryapp.RestaurantService.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fooddeliveryapp.RestaurantService.entities.Dish;
import com.fooddeliveryapp.RestaurantService.entities.Restaurant;
import com.fooddeliveryapp.RestaurantService.exceptions.DishNotFoundException;
import com.fooddeliveryapp.RestaurantService.exceptions.RestaurantNotFoundException;
import com.fooddeliveryapp.RestaurantService.models.DishRequest;
import com.fooddeliveryapp.RestaurantService.models.DishResponse;
import com.fooddeliveryapp.RestaurantService.repositories.DishRepository;
import com.fooddeliveryapp.RestaurantService.repositories.RestaurantRepository;

import lombok.extern.log4j.Log4j2;

/**
 * Service class responsible for handling business logic related to Dishes.
 * This class implements IDishService and communicates with the DishRepository to perform CRUD operations on Dish entities.
 * It contains different methods for managing dish entities.
 */
@Service
@Log4j2// Enables logging using Log4j2
public class DishService implements IDishService {
	@Autowired
	private DishRepository dishRepository;
	
	@Autowired
    private RestaurantRepository restaurantRepository;
    
    /**
     * Retrieves all dishes for the specified restaurant.
     * 
     * @param restaurantId - The ID of the restaurant whose dishes are to be fetched.
     * @return A list of DishResponse objects for the given restaurant.
     */
    @Override
    public List<DishResponse> getAllDishesForRestaurant(UUID restaurantId) {
    	log.info("Retrieving all dishes of a restaurant...");
    	
        List<Dish> dishes = dishRepository.findByRestaurant_RestaurantId(restaurantId);

        List<DishResponse> dishResponses = new ArrayList<>();
        
        for (Dish dish : dishes) {
            DishResponse dishResponse = new DishResponse();
            dishResponse.setDishId(dish.getDishId());
            dishResponse.setName(dish.getName());
            dishResponse.setPrice(dish.getPrice());
            dishResponse.setDescription(dish.getDescription());
            dishResponse.setAvailability(dish.getAvailability());

            dishResponses.add(dishResponse);
        }
        
    	log.info("All dishes of a restaurant retrieved successfully.");
        
        return dishResponses;
    }
    
    /**
     * Retrieves a specific dish by its ID.
     * This method accepts a dish ID, fetches the corresponding dish entity from the database,
     * and maps it to a DishResponse object. If the dish is not found, a custom exception is thrown.
     *
     * @param dishId - The ID of the dish to be retrieved.
     * @return A DishResponse object containing the details of the specified dish.
     * @throws DishNotFoundException if no dish is found with the given ID.
     */
	@Override
	public DishResponse getDishById(UUID dishId) {
		log.info("Retrieving dish with an ID of {} ", dishId);
		
		 Dish dish = dishRepository.findById(dishId)
	            .orElseThrow(() -> {
	            	log.error("Dish with an ID of {} not found", dishId);
	            	return new DishNotFoundException("Dish with a given ID not found. ID: " + dishId);
	            });
		 
		 DishResponse dishResponse = new DishResponse();
		 dishResponse.setDishId(dish.getDishId());
		 dishResponse.setName(dish.getName());
		 dishResponse.setPrice(dish.getPrice());
		 dishResponse.setDescription(dish.getDescription());
		 dishResponse.setAvailability(dish.getAvailability());
		 
		 log.info("Dish with an ID of {} successfully retrieved.", dishId);
		 
		 return dishResponse;
	}

    /**
     * Adds a new dish to a restaurant.
     * 
     * @param restaurantId The ID of the restaurant to which the dish will be added.
     * @param dishRequest The data for the new dish.
     * @return The UUID of the newly created dish.
     * @throws RestaurantNotFoundException if no restaurant is found with the given ID.
     */
    @Override
    public UUID addDishToRestaurant(UUID restaurantId, @Valid DishRequest dishRequest) {
    	log.info("Adding dish to a restaurant...");
    	
    	Restaurant restaurant = restaurantRepository.findById(restaurantId)
    		    .orElseThrow(() -> {
    		        log.error("Restaurant with the given ID {} not found", restaurantId);
    		        return new RestaurantNotFoundException("Restaurant with the given ID not found. ID: " + restaurantId);
    		    });

        Dish dish = Dish.builder()
                .name(dishRequest.getName())
                .price(dishRequest.getPrice())
                .description(dishRequest.getDescription())
                .availability(dishRequest.getAvailability())
                .restaurant(restaurant)
                .build();

        Dish savedDish = dishRepository.save(dish);
        
        log.info("Dish successfully added to a restaurant.");

        return savedDish.getDishId();
    }

    /**
     * Updates an existing dish for a specific restaurant.
     * This method allows partial updates to a dish.
     * If a dish with the given dishId and restaurantId doesn't exist, an exception will be thrown.
     *
     * @param dishId            The ID of the dish to be updated.
     * @param dishUpdateRequest The request object containing the updated dish data.
     * @return A DishResponse object containing the updated details of the dish.
     * @throws RestaurantNotFoundException if no restaurant is found with the given ID.
     * @throws DishNotFoundException if no dish is found with the given ID.
     */
    @Override
    public DishResponse updateDish(UUID restaurantId, UUID dishId, @Valid DishRequest dishUpdateRequest) {
    	log.info("Updating dish of a restaurant...");
    	
    	Restaurant restaurant = restaurantRepository.findById(restaurantId)
    		    .orElseThrow(() -> {
    		        log.error("Restaurant with the given ID {} not found", restaurantId);
    		        return new RestaurantNotFoundException("Restaurant with the given ID not found. ID: " + restaurantId);
    		    });

    		Dish dish = dishRepository.findById(dishId)
    		    .orElseThrow(() -> {
    		        log.error("Dish with the given ID {} not found", dishId);
    		        return new DishNotFoundException("Dish with the given ID not found. ID: " + dishId);
    		    });

        dish.setName(dishUpdateRequest.getName());
        dish.setPrice(dishUpdateRequest.getPrice());
        dish.setDescription(dishUpdateRequest.getDescription());
        dish.setAvailability(dishUpdateRequest.getAvailability());
        dish.setRestaurant(restaurant);

        Dish updatedDish = dishRepository.save(dish);

        DishResponse dishResponse = new DishResponse();
        dishResponse.setDishId(updatedDish.getDishId());
        dishResponse.setName(updatedDish.getName());
        dishResponse.setPrice(updatedDish.getPrice());
        dishResponse.setDescription(updatedDish.getDescription());
        dishResponse.setAvailability(updatedDish.getAvailability());

        return dishResponse;
    }
}
