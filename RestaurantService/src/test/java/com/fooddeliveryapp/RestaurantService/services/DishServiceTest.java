package com.fooddeliveryapp.RestaurantService.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.fooddeliveryapp.RestaurantService.entities.Dish;
import com.fooddeliveryapp.RestaurantService.entities.Restaurant;
import com.fooddeliveryapp.RestaurantService.exceptions.DishNotFoundException;
import com.fooddeliveryapp.RestaurantService.models.DishRequest;
import com.fooddeliveryapp.RestaurantService.models.DishResponse;
import com.fooddeliveryapp.RestaurantService.repositories.DishRepository;
import com.fooddeliveryapp.RestaurantService.repositories.RestaurantRepository;

@RunWith(MockitoJUnitRunner.class)
public class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private DishService dishService = new DishService();

    @DisplayName("Get All Dishes for Restaurant - Success Scenario")
    @Test
    public void test_When_Get_All_Dishes_For_Restaurant_Success() {
    	UUID restaurantId = UUID.randomUUID();
    	
    	List<Dish> dishes = prepareMockData(restaurantId);

        Mockito.when(dishRepository.findByRestaurant_RestaurantId(restaurantId)).thenReturn(dishes);

        List<DishResponse> dishResponses = dishService.getAllDishesForRestaurant(restaurantId);

        Mockito.verify(dishRepository, Mockito.times(1)).findByRestaurant_RestaurantId(restaurantId);

        assertNotNull(dishResponses);
        assertEquals(2, dishResponses.size());
        assertEquals("Dish 1", dishResponses.get(0).getName());
        assertEquals("Dish 2", dishResponses.get(1).getName());
    }

    @DisplayName("Get Dish by ID - Success Scenario")
    @Test
    public void test_When_Get_Dish_By_Id_Success() {
        UUID dishId = UUID.randomUUID();
        
        Restaurant restaurant = new Restaurant(
        	    UUID.randomUUID(),  
        	    "Restaurant A",     
        	    "123 Main Street",  
        	    "123-456-7890"      
        	);
        
        Dish dish = new Dish(dishId, restaurant, "Dish 1", 10.0, "Delicious dish 1", true);
        
        Mockito.when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));

        DishResponse dishResponse = dishService.getDishById(dishId);

        Mockito.verify(dishRepository, Mockito.times(1)).findById(dishId);

        assertNotNull(dishResponse);
        assertEquals("Dish 1", dishResponse.getName());
        assertEquals(10.0, dishResponse.getPrice(), 0.01);
    }

    @DisplayName("Get Dish by ID - Failure Scenario")
    @Test
    public void test_When_Get_Dish_By_Id_Fail_Dish_Not_Found() {
        UUID dishId = UUID.randomUUID();
        Mockito.when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        DishNotFoundException exception = assertThrows(DishNotFoundException.class, 
                () -> dishService.getDishById(dishId));

        assertEquals("Dish with a given ID not found. ID: " + dishId, exception.getMessage());

        Mockito.verify(dishRepository, Mockito.times(1)).findById(dishId);
    }
    
    @DisplayName("Add Dish To Restaurant - Success Scenario")
    @Test
    public void test_When_Add_Dish_ToRestaurant_Success() {
        UUID restaurantId = UUID.randomUUID();
        DishRequest dishRequest = new DishRequest("Dish 1", 10.0, "Delicious dish 1", true);
        Restaurant restaurant = new Restaurant(restaurantId, "Restaurant A", "123 Main Street", "123-456-7890");

        Dish savedDish = Dish.builder()
                .dishId(UUID.randomUUID())
                .name(dishRequest.getName())
                .price(dishRequest.getPrice())
                .description(dishRequest.getDescription())
                .availability(dishRequest.getAvailability())
                .restaurant(restaurant)
                .build();

        Mockito.when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        Mockito.when(dishRepository.save(Mockito.any(Dish.class))).thenReturn(savedDish);
        
        UUID result = dishService.addDishToRestaurant(restaurantId, dishRequest);

        assertNotNull(result);
        assertEquals(savedDish.getDishId(), result);
        Mockito.verify(restaurantRepository).findById(restaurantId);
        Mockito.verify(dishRepository).save(Mockito.any(Dish.class));
    }
    
    @DisplayName("Update Dish - Success Scenario")
    @Test
    public void test_When_Update_Dish_Successful() {
        UUID restaurantId = UUID.randomUUID();
        UUID dishId = UUID.randomUUID();
        
        DishRequest dishUpdateRequest = new DishRequest("Updated Dish", 15.0, "Updated description", false);

        Restaurant restaurant = new Restaurant(restaurantId, "Restaurant A", "123 Main Street", "123-456-7890");
        
        Dish existingDish = Dish.builder()
                .dishId(dishId)
                .name("Dish 1")
                .price(10.0)
                .description("Original description")
                .availability(true)
                .restaurant(restaurant)
                .build();

        Mockito.when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        Mockito.when(dishRepository.findById(dishId)).thenReturn(Optional.of(existingDish));
        Mockito.when(dishRepository.save(Mockito.any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        DishResponse result = dishService.updateDish(restaurantId, dishId, dishUpdateRequest);

        assertNotNull(result);
        assertEquals(dishUpdateRequest.getName(), result.getName());
        assertEquals(dishUpdateRequest.getPrice(), result.getPrice());
        assertEquals(dishUpdateRequest.getDescription(), result.getDescription());
        assertEquals(dishUpdateRequest.getAvailability(), result.getAvailability());
        Mockito.verify(restaurantRepository).findById(restaurantId);
        Mockito.verify(dishRepository).findById(dishId);
        Mockito.verify(dishRepository).save(Mockito.any(Dish.class));
    }
    
    private List<Dish> prepareMockData(UUID restaurantId) {
        Restaurant restaurant = new Restaurant(
            restaurantId, 
            "Restaurant A",     
            "123 Main Street", 
            "123-456-7890"      
        );

        Dish dish1 = new Dish(
            UUID.randomUUID(),  
            restaurant,       
            "Dish 1",           
            10.0,               
            "Delicious dish 1", 
            true               
        );

        Dish dish2 = new Dish(
            UUID.randomUUID(),  
            restaurant,         
            "Dish 2",           
            12.0,              
            "Tasty dish 2",   
            true                
        );

        return Arrays.asList(dish1, dish2);
    }
}

