package com.example.fooddeliveryapp.OrderService.services;

import java.util.UUID;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
import com.example.fooddeliveryapp.OrderService.models.OrderRequest;

import jakarta.validation.Valid;

/**
 * Interface for the Order service that defines the operations 
 * related to order management. This interface acts as a contract 
 * for the implementation of order-related business logic.
 */
public interface IOrderService {

	UUID placeOrder(@Valid OrderRequest orderRequest);

	void removeItemFromOrder(UUID orderId, UUID itemId);

	void updateOrderStatus(UUID orderId, OrderStatus newStatus);
}
