package com.example.fooddeliveryapp.OrderService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
import com.example.fooddeliveryapp.OrderService.exceptions.OrderNotFoundException;
import com.example.fooddeliveryapp.OrderService.external.constants.DeliveryStatus;
import com.example.fooddeliveryapp.OrderService.external.events.DeliveryEvent;
import com.example.fooddeliveryapp.OrderService.repositories.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DeliveryEventListener {
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
   
    @Autowired
    public DeliveryEventListener(OrderRepository orderRepository, ObjectMapper objectMapper) {
    	this.orderRepository = orderRepository;
    	this.objectMapper = objectMapper;
    }

    /**
     * Kafka listener method that listens for delivery events on the "delivery-topic".
     * The method processes delivery events dispatched from delivery service and updates the status of the corresponding order.
     * 
     * @param message The raw message containing delivery event details.
     */
    @KafkaListener(topics = "delivery-topic", groupId = "order-group")
    public void handleDeliveryEvent(String message) {
        try {
            // deserializing message to DeliveryEvent
            DeliveryEvent event = objectMapper.readValue(message, DeliveryEvent.class);
            log.info("Received delivery event for order: {}", event.getOrderId());

            orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order with ID {} not found", event.getOrderId());
                    return new OrderNotFoundException("Order with an ID of " + event.getOrderId() + " not found");
                });

            if (event.getStatus() == DeliveryStatus.DELIVERED) {
                orderRepository.updateOrderStatus(event.getOrderId(), OrderStatus.DELIVERED);
                log.info("Order with ID {} successfully DELIVERED", event.getOrderId());
            } else if (event.getStatus() == DeliveryStatus.FAILED) {
                orderRepository.updateOrderStatus(event.getOrderId(), OrderStatus.CANCELED);
                log.info("Order with ID {} has been CANCELED", event.getOrderId());
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing the event: {}", e.getMessage());
        }
    }
}
