package com.fooddeliveryapp.DeliveryService.events;

import java.util.UUID;

import com.fooddeliveryapp.DeliveryService.constants.DeliveryStatus;

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