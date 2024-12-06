package com.example.fooddeliveryapp.OrderService.external.events;

import java.util.UUID;

import com.example.fooddeliveryapp.OrderService.external.constants.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an event related to a delivery.
 * This event is used to communicate the delivery status of a delivery between Delivery and order services.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryEvent {
    private UUID orderId;
    private DeliveryStatus status;
}
