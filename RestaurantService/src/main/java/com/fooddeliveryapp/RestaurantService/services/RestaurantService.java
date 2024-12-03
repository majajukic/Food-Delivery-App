package com.fooddeliveryapp.RestaurantService.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fooddeliveryapp.RestaurantService.entities.Restaurant;
import com.fooddeliveryapp.RestaurantService.exceptions.RestaurantNotFoundException;
import com.fooddeliveryapp.RestaurantService.models.RestaurantRequest;
import com.fooddeliveryapp.RestaurantService.models.RestaurantResponse;
import com.fooddeliveryapp.RestaurantService.repositories.RestaurantRepository;

import lombok.extern.log4j.Log4j2;

/**
 * Service class responsible for handling business logic related to Restaurants.
 * This class implements IRestaurantService and communicates with the RestaurantRepository to perform CRUD operations on Restaurant entities.
 * It contains different methods for managing restaurant entities.
 */
@Service
@Log4j2// Enables logging using Log4j2
public class RestaurantService implements IRestaurantService {
	
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	/**
	* Retrieves all restaurants from the database.
	* This method fetches all restaurant entities and maps them to RestaurantResponse objects.
	* 
	* @return A list of RestaurantResponse objects containing details of all restaurants.
	*/
	@Override
	public List<RestaurantResponse> getAllRestaurants() {
		log.info("Getting all restaurants...");
	
	    List<Restaurant> restaurants = restaurantRepository.findAll();
	
	    List<RestaurantResponse> restaurantResponses = new ArrayList<>();
	    
	    for (Restaurant restaurant : restaurants) {
	        RestaurantResponse restaurantResponse = new RestaurantResponse();
	        BeanUtils.copyProperties(restaurant, restaurantResponse);
	        restaurantResponses.add(restaurantResponse);
	    }
	    
	    log.info("Restaurants retrieved successfully.");
	
	    return restaurantResponses;
	}
	
	/**
	* Retrieves a restaurant by its UUID from the database.
	* This method fetches the restaurant entity by its ID and maps it to a RestaurantResponse.
	* If the restaurant with the given ID is not found, it throws a RuntimeException.
	* 
	* @param restaurantId - The UUID of the restaurant to retrieve.
	* @return A RestaurantResponse object containing the restaurant details.
	* @throws RuntimeException if the restaurant with the given ID is not found in the database.
	*/ 
	@Override
	public RestaurantResponse getRestaurantById(UUID restaurantId) {
		log.info("Getting the restaurant by its ID: {}...", restaurantId);
		
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with the given ID not found. ID: " + restaurantId));
		
		RestaurantResponse restaurantResponse = new RestaurantResponse();
		BeanUtils.copyProperties(restaurant, restaurantResponse);
		
		log.info("Restaurant with an ID of {} successfully retrieved.", restaurantId);
		
		return restaurantResponse;
	}
	
	 /**
	 * Adds a new restaurant to the database.
	 * This method receives a RestaurantRequest model, maps it to a Restaurant entity,
	 * saves the restaurant to the database, and returns the restaurant's ID.
	 * 
	 * @param restaurantRequest model containing the restaurant data to be added.
	 * @return The ID of the newly created restaurant.
	 */
	 @Override
	 public UUID addRestaurant(@Valid RestaurantRequest restaurantRequest) {
		log.info("Adding a new restaurant...");
		
		Restaurant restaurant 
				= Restaurant.builder()
				.name(restaurantRequest.getName())
				.address(restaurantRequest.getAddress())
				.phoneNumber(restaurantRequest.getPhoneNumber())
				.build();
		
		restaurantRepository.save(restaurant);
		
		log.info("Restaurant added successfully.");
		
		return restaurant.getRestaurantId();
	}
	
	/**
	 * Updates a restaurant by its ID with the given data.
	 * This method looks for the restaurant by its ID, updates its fields based on the provided request, and saves it back to the database.
	 * Only the fields that are provided in the request will be updated.
	 * 
	 * @param restaurantId - The ID of the restaurant to be updated.
	 * @param restaurantRequest - The data to update the restaurant (name, address, phone number, etc.).
	 * @return The updated restaurant response with the updated data.
	 * @throws RuntimeException if the restaurant with the given ID is not found in the database.
	 */
	 @Override
	 public RestaurantResponse updateRestaurant(UUID restaurantId, @Valid RestaurantRequest restaurantRequest) {
	    log.info("Updating the restaurant with ID: {}", restaurantId);

	    Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with the given ID not found. ID: " + restaurantId));

	    restaurant.setName(restaurantRequest.getName());
	    restaurant.setAddress(restaurantRequest.getAddress());
	    restaurant.setPhoneNumber(restaurantRequest.getPhoneNumber());

	    restaurantRepository.save(restaurant);

	    RestaurantResponse restaurantResponse = new RestaurantResponse();
	    BeanUtils.copyProperties(restaurant, restaurantResponse);

	    log.info("Restaurant with ID {} updated successfully.", restaurantId);

	    return restaurantResponse;
	}

	 /**
	  * Deletes a restaurant by its ID.
	  * This method checks if the restaurant exists in the database. If it does, it deletes it;
	  * if not, it throws an exception and logs an error.
	  *
	  * @param restaurantId - The ID of the restaurant to be deleted.
	  * @throws RuntimeException if the restaurant with the given ID is not found in the database.
	  */
	 @Override
	 public void deleteRestaurant(UUID restaurantId) {
	     log.info("Deleting the restaurant with ID: {}", restaurantId);
	     
	     if (restaurantRepository.existsById(restaurantId)) {
	         restaurantRepository.deleteById(restaurantId);
	         log.info("Restaurant with ID {} deleted successfully.", restaurantId);
	     } else {
	         log.error("Restaurant with ID {} does not exist.", restaurantId);
	         throw new RestaurantNotFoundException("Restaurant with the given ID not found. ID: " + restaurantId);
	     }
	 }
}
