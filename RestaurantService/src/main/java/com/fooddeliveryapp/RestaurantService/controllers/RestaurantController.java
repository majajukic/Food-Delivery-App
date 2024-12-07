package com.fooddeliveryapp.RestaurantService.controllers;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddeliveryapp.RestaurantService.models.DishRequest;
import com.fooddeliveryapp.RestaurantService.models.DishResponse;
import com.fooddeliveryapp.RestaurantService.models.RestaurantRequest;
import com.fooddeliveryapp.RestaurantService.models.RestaurantResponse;
import com.fooddeliveryapp.RestaurantService.services.IDishService;
import com.fooddeliveryapp.RestaurantService.services.IRestaurantService;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

	private final IRestaurantService restaurantService;
	private final IDishService dishService;
	
	@Autowired
	public RestaurantController(IRestaurantService restaurantService, IDishService dishService) {
		this.restaurantService = restaurantService;
		this.dishService = dishService;
	}
	
	/**
	 * Retrieves all restaurants from the database.
	 * This method retrieves a list of all restaurants and returns them in the response.
	 * Users who are either Admin or Customer can view all restaurants
	 * 
	 * @return A response containing a list of RestaurantResponse objects and an OK status (200).
	 */
	@PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
	@GetMapping
	public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
	    List<RestaurantResponse> restaurantResponses = restaurantService.getAllRestaurants();
	    
	    return new ResponseEntity<>(restaurantResponses, HttpStatus.OK);
	}
	
	/**
	 * Retrieves a restaurant by its ID from the database.
	 * This method accepts a restaurant ID as a path variable, fetches the corresponding restaurant entity,
	 * and returns the restaurant details in the response body.
	 * Users who are eaither Admin or Customer can view restaurant details
	 *
	 * @param restaurantId - The ID of the restaurant to retrieve.
	 * @return A response containing the restaurant details and an OK status (200).
	 */
	@PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
	@GetMapping("/{id}")
	public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable("id") UUID restaurantId) {
		RestaurantResponse restaurantResponse = restaurantService.getRestaurantById(restaurantId);
		
		return new ResponseEntity<>(restaurantResponse, HttpStatus.OK);
	}
	
	 /**
     * Adds a new restaurant to the database.
     * This method receives a restaurant request object in the request body,
     * creates a new restaurant entity in the database, and returns the restaurant ID.
     * Only a user with Admin role should be able to add a new restaurant
     *
     * @param restaurantRequest - The data needed to create a new restaurant.
     * @return A response containing the ID of the newly created restaurant and a CREATED status (201).
     */
	@PreAuthorize("hasAuthority('Admin')")
	@PostMapping
	public ResponseEntity<UUID> addRestaurant(@Valid @RequestBody RestaurantRequest restaurantRequest) {
		UUID restaurantId = restaurantService.addRestaurant(restaurantRequest);
		
		return new ResponseEntity<>(restaurantId, HttpStatus.CREATED);
	}
	
	/**
	 * Updates an existing restaurant in the database.
	 * This method receives a restaurant ID as a path variable and a restaurant request body containing the updated data.
	 * It calls the service to update the restaurant and returns the updated restaurant information along with a OK status (200).
	 * Only a user with a role of Admin should be able to update restaurant data
	 *
	 * @param restaurantId - The ID of the restaurant to be updated.
	 * @param restaurantRequest - The request body containing the updated data for the restaurant.
	 * @return A response containing the updated restaurant information and a OK status (200).
	 */
	@PreAuthorize("hasAuthority('Admin')")
	@PatchMapping("/{id}")
	public ResponseEntity<RestaurantResponse> updateRestaurant(@PathVariable("id") UUID restaurantId, @Valid @RequestBody RestaurantRequest restaurantRequest) {
	    RestaurantResponse updatedRestaurant = restaurantService.updateRestaurant(restaurantId, restaurantRequest);
	    
	    return new ResponseEntity<>(updatedRestaurant, HttpStatus.OK); 
	}
	
	/**
	 * Deletes a restaurant from the database.
	 * This method receives a restaurant ID as a path variable, calls the service to delete the restaurant,
	 * and returns a NO_CONTENT status (204) if the restaurant is successfully deleted.
	 * Only a user with a role of Admin should be able to delete a restaurant
	 *
	 * @param restaurantId - The ID of the restaurant to be deleted.
	 * @return A response with a NO_CONTENT status (204) indicating successful deletion.
	 */
	@PreAuthorize("hasAuthority('Admin')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") UUID restaurantId) {
	    restaurantService.deleteRestaurant(restaurantId);
	    
	    return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
	}
	
	 /**
     * Retrieves all dishes for a specific restaurant.
     * This method fetches all dishes that belong to a restaurant identified by the restaurant ID.
     * Users who are either Admin or Customer can view all dishes of a restaurant
     * 
     * @param restaurantId - The ID of the restaurant for which the dishes are to be fetched.
     * @return A list of dishes belonging to the specified restaurant.
     */
	@PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
    @GetMapping("/{id}/dishes")
    public ResponseEntity<List<DishResponse>> getAllDishes(@PathVariable("id") UUID restaurantId) {
        List<DishResponse> dishes = dishService.getAllDishesForRestaurant(restaurantId);
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }
    
    /**
     * Retrieves a specific dish by its ID.
     * This method accepts a dish ID as a path variable, fetches the corresponding dish entity,
     * and returns the dish details in the response body.
     * SCOPE_internal indicates that this endpoint should be exposed to another calling service (Order service)
     * Alongise that, users who are either Admin or Customer can get dish details
     *
     * @param dishId - The ID of the dish to retrieve.
     * @return A response containing the dish details and an OK status (200).
     */
	@PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer') || hasAuthority('SCOPE_internal')")
    @GetMapping("/dishes/{dishId}")
    public ResponseEntity<DishResponse> getDishById(@PathVariable UUID dishId) {
        DishResponse dishResponse = dishService.getDishById(dishId);
        
        return new ResponseEntity<>(dishResponse, HttpStatus.OK);
    }
	
	/**
     * Adds a new dish to a restaurant.
     * This method accepts a DishRequest model, maps it to a Dish entity,
     * and saves it to the database under the specified restaurant.
     * Only a user with a role of Admin should be able to add new dishes to restaurants
     *
     * @param restaurantId - The ID of the restaurant to which the dish will be added.
     * @param dishRequest - The data needed to create a new dish.
     * @return A response containing the ID of the newly created dish for a restaurant and a CREATED status (201).
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/{id}/dishes")
    public ResponseEntity<UUID> addDishToRestaurant(@PathVariable("id") UUID restaurantId, @Valid @RequestBody DishRequest dishRequest) {
        UUID dishId = dishService.addDishToRestaurant(restaurantId, dishRequest);
        
        return new ResponseEntity<>(dishId, HttpStatus.CREATED);
    }
    
    /**
     * Updating the details of a specific dish that belongs to a restaurant.
     * This method uses the provided dishId and restaurantId to locate the dish.
     * The dish's details are updated based on the data provided in the request body.
     * Only a user with a role of Admin should be able to update dishes' data
     * 
     * @param restaurantId The ID of the restaurant where the dish belongs.
     * @param dishId       The ID of the dish to be updated.
     * @param dishRequest  The request body containing the updated dish information.
     * @return A ResponseEntity containing the updated dish details and an HTTP status code of 200 OK if successful.
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PatchMapping("/{id}/dishes/{dishId}")
    public ResponseEntity<DishResponse> updateDish(@PathVariable("id") UUID restaurantId, @PathVariable UUID dishId, @Valid @RequestBody DishRequest dishRequest) {
        DishResponse updatedDish = dishService.updateDish(restaurantId, dishId, dishRequest);

        return new ResponseEntity<>(updatedDish, HttpStatus.OK); 
    }
}

