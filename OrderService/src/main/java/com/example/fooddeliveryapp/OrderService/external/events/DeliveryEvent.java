package com.example.fooddeliveryapp.OrderService.external.events;

import java.util.UUID;

import com.example.fooddeliveryapp.OrderService.external.constants.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryEvent {
    private UUID orderId;
    private DeliveryStatus status;
}
