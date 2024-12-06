package com.fooddeliveryapp.DeliveryService.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddeliveryapp.DeliveryService.constants.DeliveryStatus;
import com.fooddeliveryapp.DeliveryService.entities.DeliveryDetails;
import com.fooddeliveryapp.DeliveryService.events.DeliveryEvent;
import com.fooddeliveryapp.DeliveryService.exceptions.DeliveryNotFoundException;
import com.fooddeliveryapp.DeliveryService.models.DeliveryRequest;
import com.fooddeliveryapp.DeliveryService.models.DeliveryResponse;
import com.fooddeliveryapp.DeliveryService.repositories.DeliveryRepository;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DeliveryService implements IDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.deliveryRepository = deliveryRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Fetches delivery details by order ID.
     * This method retrieves the delivery information associated with a given order ID from the database.
     * If no delivery details are found for the provided order ID, it throws a RuntimeException.
     * 
     * @param orderId The unique identifier of the order whose delivery details are to be fetched.
     * @return A DeliveryResponse object containing the details of the delivery.
     * @throws DeliveryNotFoundException if the delivery for the given ID is not found in the database
     */
    @Override
	public DeliveryResponse getDeliveryByOrderId(UUID orderId) {
    	DeliveryDetails deliveryDetails = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Delivery with an order ID of " + orderId + " not found.");
                    return new DeliveryNotFoundException("Delivery not found for Order ID: " + orderId);
                });

        
        DeliveryResponse deliveryResponse = DeliveryResponse.builder()
        		.deliveryId(deliveryDetails.getDeliveryId())
        		.deliveryStatus(deliveryDetails.getDeliveryStatus())
        		.initiatedAt(deliveryDetails.getInitiatedAt())
        		.deliveredAt(deliveryDetails.getDeliveredAt())
        		.build();
        
        return deliveryResponse;
	}
    
    /**
     * Processes a new delivery request.
     * This method initiates a delivery process by saving the delivery details to the database.
     * It then simulates a delivery process in a separate thread, where the status is updated to "DELIVERED" after a delay.
     * If the delivery is successful, an event is sent to a Kafka topic to notify the Order service.
     * If the delivery fails, the status is updated to "FAILED" and a failure event is sent to Kafka to notify the Order service.
     * 
     * @param deliveryRequest The delivery request containing the necessary information to initiate the delivery.
     */
    @Override
    public void processDelivery(@Valid DeliveryRequest deliveryRequest) {
        log.info("Initiating the delivery process...");
        
        DeliveryDetails delivery = DeliveryDetails.builder()
            .orderId(deliveryRequest.getOrderId())
            .restaurantId(deliveryRequest.getRestaurantId())
            .userId(deliveryRequest.getUserId())
            .deliveryStatus(DeliveryStatus.IN_PROGRESS)
            .initiatedAt(LocalDateTime.now())
            .build();
        deliveryRepository.save(delivery);
        
        // simulating delivery process in a separate thread
        new Thread(() -> {
            try {
                Thread.sleep(10000); 
                delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
                delivery.setDeliveredAt(LocalDateTime.now());
                deliveryRepository.save(delivery);
                
                log.info("Dispatching delivery event to Order service...");
                
                DeliveryEvent event = new DeliveryEvent(delivery.getOrderId(), DeliveryStatus.DELIVERED);
                String value = objectMapper.writeValueAsString(event);
                
                kafkaTemplate.send("delivery-topic", value);
                
                log.info("Delivery event successfully dispatched to Order service.");
            } catch (InterruptedException e) {
                log.error("Delivery failed.");
                delivery.setDeliveryStatus(DeliveryStatus.FAILED);
                deliveryRepository.save(delivery);
                
                DeliveryEvent event = new DeliveryEvent(delivery.getOrderId(), DeliveryStatus.FAILED);
                try {
                    String value = objectMapper.writeValueAsString(event);
                    kafkaTemplate.send("delivery-topic", value);
                } catch (JsonProcessingException e1) {
                    log.error("Error serializing the failure event: {}", e1.getMessage());
                }
                
                Thread.currentThread().interrupt();
            } catch (JsonProcessingException e) {
                log.error("Error serializing the event: {}", e.getMessage());
            }
        }).start();
    }
}

