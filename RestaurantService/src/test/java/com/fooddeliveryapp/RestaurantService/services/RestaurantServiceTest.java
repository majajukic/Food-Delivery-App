package com.fooddeliveryapp.RestaurantService.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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

import com.fooddeliveryapp.RestaurantService.entities.Restaurant;
import com.fooddeliveryapp.RestaurantService.exceptions.RestaurantNotFoundException;
import com.fooddeliveryapp.RestaurantService.models.RestaurantRequest;
import com.fooddeliveryapp.RestaurantService.models.RestaurantResponse;
import com.fooddeliveryapp.RestaurantService.repositories.RestaurantRepository;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService = new RestaurantService();

    @DisplayName("Get All Restaurants - Success Scenario")
    @Test
    public void test_When_Get_All_Restaurants_Success() {
        List<Restaurant> restaurants = prepareMockRestaurants();

        Mockito.when(restaurantRepository.findAll()).thenReturn(restaurants);

        List<RestaurantResponse> restaurantResponses = restaurantService.getAllRestaurants();

        Mockito.verify(restaurantRepository, Mockito.times(1)).findAll();

        assertNotNull(restaurantResponses);
        assertEquals(2, restaurantResponses.size());
        assertEquals("Restaurant A", restaurantResponses.get(0).getName());
        assertEquals("Restaurant B", restaurantResponses.get(1).getName());
    }

    @DisplayName("Get Restaurant by ID - Success Scenario")
    @Test
    public void test_When_Get_Restaurant_By_Id_Success() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant(restaurantId, "Restaurant A", "123 Main Street", "123-456-7890");

        Mockito.when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        RestaurantResponse restaurantResponse = restaurantService.getRestaurantById(restaurantId);

        Mockito.verify(restaurantRepository, Mockito.times(1)).findById(restaurantId);

        assertNotNull(restaurantResponse);
        assertEquals("Restaurant A", restaurantResponse.getName());
    }

    @DisplayName("Get Restaurant by ID - Failure Scenario")
    @Test
    public void test_When_Get_Restaurant_By_Id_Fail() {
        UUID restaurantId = UUID.randomUUID();
        Mockito.when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        RestaurantNotFoundException exception = assertThrows(RestaurantNotFoundException.class, 
                () -> restaurantService.getRestaurantById(restaurantId));

        assertEquals("Restaurant with the given ID not found. ID: " + restaurantId, exception.getMessage());

        Mockito.verify(restaurantRepository, Mockito.times(1)).findById(restaurantId);
    }

    @DisplayName("Add Restaurant - Success Scenario")
    @Test
    public void test_When_Add_Restaurant_Success() {
        RestaurantRequest restaurantRequest = new RestaurantRequest("Restaurant A", "123 Main Street", "123-456-7890");
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(UUID.randomUUID())
                .name(restaurantRequest.getName())
                .address(restaurantRequest.getAddress())
                .phoneNumber(restaurantRequest.getPhoneNumber())
                .build();

        Mockito.when(restaurantRepository.save(Mockito.any(Restaurant.class))).thenReturn(restaurant);

        UUID result = restaurantService.addRestaurant(restaurantRequest);

        assertEquals(restaurant.getRestaurantId(), result);

        Mockito.verify(restaurantRepository, Mockito.times(1)).save(Mockito.any(Restaurant.class));
    }

    @DisplayName("Update Restaurant - Success Scenario")
    @Test
    public void test_When_Update_Restaurant_Success() {
        UUID restaurantId = UUID.randomUUID();
        RestaurantRequest restaurantRequest = new RestaurantRequest("Updated Restaurant", "456 Another Street", "987-654-3210");
        Restaurant existingRestaurant = new Restaurant(restaurantId, "Restaurant A", "123 Main Street", "123-456-7890");

        Mockito.when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));
        Mockito.when(restaurantRepository.save(Mockito.any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RestaurantResponse restaurantResponse = restaurantService.updateRestaurant(restaurantId, restaurantRequest);

        assertNotNull(restaurantResponse);
        assertEquals("Updated Restaurant", restaurantResponse.getName());
        assertEquals("456 Another Street", restaurantResponse.getAddress());
        assertEquals("987-654-3210", restaurantResponse.getPhoneNumber());

        Mockito.verify(restaurantRepository, Mockito.times(1)).findById(restaurantId);
        Mockito.verify(restaurantRepository, Mockito.times(1)).save(Mockito.any(Restaurant.class));
    }

    @DisplayName("Delete Restaurant - Success Scenario")
    @Test
    public void test_When_Delete_Restaurant_Success() {
        UUID restaurantId = UUID.randomUUID();

        Mockito.when(restaurantRepository.existsById(restaurantId)).thenReturn(true);

        restaurantService.deleteRestaurant(restaurantId);

        Mockito.verify(restaurantRepository, Mockito.times(1)).existsById(restaurantId);
        Mockito.verify(restaurantRepository, Mockito.times(1)).deleteById(restaurantId);
    }

    @DisplayName("Delete Restaurant - Failure Scenario")
    @Test
    public void test_When_Delete_Restaurant_Fail() {
        UUID restaurantId = UUID.randomUUID();

        Mockito.when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

        RestaurantNotFoundException exception = assertThrows(RestaurantNotFoundException.class, 
                () -> restaurantService.deleteRestaurant(restaurantId));

        assertEquals("Restaurant with the given ID not found. ID: " + restaurantId, exception.getMessage());

        Mockito.verify(restaurantRepository, Mockito.times(1)).existsById(restaurantId);
        Mockito.verify(restaurantRepository, Mockito.never()).deleteById(Mockito.any(UUID.class));
    }

    private List<Restaurant> prepareMockRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(new Restaurant(UUID.randomUUID(), "Restaurant A", "123 Main Street", "123-456-7890"));
        restaurants.add(new Restaurant(UUID.randomUUID(), "Restaurant B", "456 Another Street", "987-654-3210"));
        return restaurants;
    }
}
