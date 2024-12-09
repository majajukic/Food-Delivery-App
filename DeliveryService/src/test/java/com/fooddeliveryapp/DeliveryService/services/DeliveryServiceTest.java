package com.fooddeliveryapp.DeliveryService.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddeliveryapp.DeliveryService.constants.DeliveryStatus;
import com.fooddeliveryapp.DeliveryService.entities.DeliveryDetails;
import com.fooddeliveryapp.DeliveryService.events.DeliveryEvent;
import com.fooddeliveryapp.DeliveryService.exceptions.DeliveryNotFoundException;
import com.fooddeliveryapp.DeliveryService.models.DeliveryRequest;
import com.fooddeliveryapp.DeliveryService.models.DeliveryResponse;
import com.fooddeliveryapp.DeliveryService.repositories.DeliveryRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private DeliveryService deliveryService;
    
    private UUID orderId;
    private DeliveryDetails deliveryDetails;

    @Before
    public void setUp() {
        orderId = UUID.randomUUID();
        deliveryDetails = new DeliveryDetails();
        deliveryDetails.setDeliveryId(UUID.randomUUID());
        deliveryDetails.setOrderId(orderId);
        deliveryDetails.setRestaurantId(UUID.randomUUID());
        deliveryDetails.setUserId("user@gmail.com");
        deliveryDetails.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);
        deliveryDetails.setInitiatedAt(LocalDateTime.now());
        deliveryDetails.setDeliveredAt(LocalDateTime.now().plusDays(1));
    }

    @DisplayName("Get Delivery Details By Order ID - Success Scenario")
    @Test
    public void test_When_Get_Delivery_By_OrderId_Success() {
        Mockito.when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(deliveryDetails));
        
        DeliveryResponse deliveryResponse = deliveryService.getDeliveryByOrderId(orderId);

        assertNotNull(deliveryResponse);
        assertEquals(deliveryDetails.getDeliveryId(), deliveryResponse.getDeliveryId());
        assertEquals(deliveryDetails.getDeliveryStatus(), deliveryResponse.getDeliveryStatus());
        assertEquals(deliveryDetails.getInitiatedAt(), deliveryResponse.getInitiatedAt());
        assertEquals(deliveryDetails.getDeliveredAt(), deliveryResponse.getDeliveredAt());
    }

    @DisplayName("Get Delivery by Order ID - Failure Scenario")
    @Test
    public void test_When_Get_Delivery_By_OrderId_Delivery_Not_Found() {
        Mockito.when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        DeliveryNotFoundException exception = assertThrows(DeliveryNotFoundException.class,
                () -> deliveryService.getDeliveryByOrderId(orderId));

        assertEquals("Delivery not found for Order ID: " + orderId, exception.getMessage());

        Mockito.verify(deliveryRepository, Mockito.times(1)).findByOrderId(orderId);
    }

    @DisplayName("Process Delivery - Success Scenario")
    @Test
    public void test_When_Process_Delivery_Success() throws JsonProcessingException {
        DeliveryRequest deliveryRequest = new DeliveryRequest(UUID.randomUUID(), "user@gmail.com", UUID.randomUUID());
        
        Mockito.when(deliveryRepository.save(Mockito.any(DeliveryDetails.class))).thenReturn(deliveryDetails);  
        Mockito.when(objectMapper.writeValueAsString(Mockito.any(DeliveryEvent.class))).thenReturn("{}");

        deliveryService.processDelivery(deliveryRequest);
        
        Mockito.verify(deliveryRepository, Mockito.times(1)).save(Mockito.any(DeliveryDetails.class));
    }

    @DisplayName("Process Delivery - Failure Scenario (JsonProcessingException)")
    @Test
    public void test_When_Process_Delivery_JsonProcessingException() throws JsonProcessingException {
        DeliveryRequest deliveryRequest = new DeliveryRequest(UUID.randomUUID(), "user@gmail.com", UUID.randomUUID());
        
        Mockito.when(deliveryRepository.save(Mockito.any(DeliveryDetails.class))).thenReturn(deliveryDetails);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any(DeliveryEvent.class))).thenThrow(new JsonProcessingException("Error") {});

        deliveryService.processDelivery(deliveryRequest);

        Mockito.verify(deliveryRepository, Mockito.times(1)).save(Mockito.any(DeliveryDetails.class));
    }

    @DisplayName("Process Delivery - Failure Scenario (InterruptedException)")
    @Test
    public void test_When_Process_Delivery_InterruptedException() throws JsonProcessingException, InterruptedException {
    	DeliveryRequest deliveryRequest = new DeliveryRequest(UUID.randomUUID(), "user@gmail.com", UUID.randomUUID());

        Mockito.when(deliveryRepository.save(Mockito.any(DeliveryDetails.class))).thenReturn(deliveryDetails);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any(DeliveryEvent.class))).thenReturn("{}");

        Thread.sleep(100);

        deliveryService.processDelivery(deliveryRequest);

        Mockito.verify(deliveryRepository, Mockito.times(1)).save(Mockito.any(DeliveryDetails.class));
    }
}
