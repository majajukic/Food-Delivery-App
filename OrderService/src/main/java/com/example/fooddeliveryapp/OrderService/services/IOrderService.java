package com.example.fooddeliveryapp.OrderService.services;

import java.util.UUID;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
import com.example.fooddeliveryapp.OrderService.models.OrderRequest;
import com.example.fooddeliveryapp.OrderService.models.OrderResponse;

import jakarta.validation.Valid;

/**
 * Interface for the Order service that defines the operations 
 * related to order management. This interface acts as a contract 
 * for the implementation of order-related business logic.
 */
public interface IOrderService {

	UUID processOrder(@Valid OrderRequest orderRequest, String userEmail);

	OrderResponse getOrderDetails(UUID orderId);
}
