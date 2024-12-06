package com.fooddeliveryapp.DeliveryService.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddeliveryapp.DeliveryService.constants.DeliveryStatus;
import com.fooddeliveryapp.DeliveryService.entities.DeliveryDetails;
import com.fooddeliveryapp.DeliveryService.events.DeliveryEvent;
import com.fooddeliveryapp.DeliveryService.models.DeliveryRequest;
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

