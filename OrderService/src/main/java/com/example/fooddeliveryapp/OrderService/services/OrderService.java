package com.example.fooddeliveryapp.OrderService.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
import com.example.fooddeliveryapp.OrderService.entities.Order;
import com.example.fooddeliveryapp.OrderService.entities.OrderItem;
import com.example.fooddeliveryapp.OrderService.exceptions.EmptyOrderException;
import com.example.fooddeliveryapp.OrderService.exceptions.OrderItemNotFoundException;
import com.example.fooddeliveryapp.OrderService.exceptions.OrderNotFoundException;
import com.example.fooddeliveryapp.OrderService.models.OrderRequest;
import com.example.fooddeliveryapp.OrderService.repositories.OrderRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

/**
 * Service class responsible for handling business logic related to Orders.
 * This class implements IOrderService and communicates with the OrderRepository to perform CRUD operations on Order entities.
 * It contains different methods for managing order entities.
 */
@Service
@Log4j2
public class OrderService implements IOrderService{
	@Autowired
	private OrderRepository orderRepository;
	
	/**
	 * Places a new order in the system.
	 * This method receives an order request object containing the user's details,
	 * restaurant information, and items in the order. It performs the following actions:
	 * 
	 * 1. Checks if the order contains valid items (at least one item must be included).
	 * 2. Calculates the total price of the order based on the items and their quantities.
	 * 3. Creates a new order and its associated order items in the database.
	 * 4. Saves the order in the repository and returns the ID of the newly created order.
	 * 
	 * Future implementation (communication with other services):
	 * - Check if the dishes in the order are available in the corresponding restaurant.
	 * - Prevent cross-restaurant orders by verifying that all items belong to the same restaurant.
	 * - Integrate payment processing (once implemented), including support for different payment methods (CASH, CARD).
	 * - Upon successful payment, update the order status to "DELIVERING" and initiate the delivery process.
	 * - If the delivery process is completed, update the order status to "DELIVERED".
	 * 
	 * @param orderRequest - The data needed to create a new order, including user and restaurant details, 
	 *                       as well as the items in the order.
	 * @return The ID of the newly created order.
	 */
	@Override
	public UUID placeOrder(@Valid OrderRequest orderRequest) {	
        log.info("Placing an order...");
        
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            log.error("Cannot save an order with no items.");
            throw new EmptyOrderException("Order must have at least one item."); 
        }

        Double totalPrice = orderRequest.getItems().stream()
        	    .map(item -> item.getPrice() * item.getQuantity()) 
        	    .reduce(0.0, Double::sum);

        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        order.setRestaurantId(orderRequest.getRestaurantId());
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PLACED);
        order.setTimestamp(LocalDateTime.now());

        List<OrderItem> orderItems = orderRequest.getItems().stream()
            .map(itemRequest -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setDishId(itemRequest.getDishId());
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPrice(itemRequest.getPrice());
                orderItem.setOrder(order); 
                return orderItem;
            })
            .toList();

        order.setOrderItems(orderItems);

        orderRepository.save(order);
        
        log.info("Order placed successfully.");
        
        return order.getOrderId();
	}
	
	/**
	 * Updates the status of an existing order.
	 * 
	 * This method retrieves the order using the provided order ID, updates its status
	 * to the specified new status, and saves the updated order back to the database.
	 * 
	 * @param orderId - The ID of the order whose status needs to be updated.
	 * @param newStatus - The new status to set for the order.
	 */
	@Override
	public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {    
	    log.info("Updating order status for an order with an ID of {}", orderId);

	    Order order = orderRepository.findById(orderId)
	    		.orElseThrow(() -> {
	                log.error("Order with ID {} not found", orderId);
	                return new OrderNotFoundException("Order with an ID of " + orderId + " not found");
	            });

	    order.setStatus(newStatus);

	    orderRepository.save(order);

	    log.info("Status update for an order with an ID of {} successful", orderId);
	}

	/**
	 * Removes an item from the order.
	 * This method receives ID of the order an ID of an item to be removed from that order,
	 * restaurant information, and items in the order. It performs the following actions:
	 * 
	 * @param orderId - The ID of the order from which the item should be removed.
	 * @param itemId - The ID of the item to be removed.
	 */
	@Override
    public void removeItemFromOrder(UUID orderId, UUID itemId) {
        log.info("Removing item with an ID of {} from order", itemId);
        
        Order order = orderRepository.findById(orderId)
	    		.orElseThrow(() -> {
	                log.error("Order with ID {} not found", orderId);
	                return new OrderNotFoundException("Order with an ID of " + orderId + " not found");
	            });

        OrderItem itemToRemove = order.getOrderItems().stream()
            .filter(item -> item.getOrderItemId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> {
            	log.error("Order item with ID {} not found", itemId);
                return new OrderItemNotFoundException("Item with an ID of " + itemId + " not found in the order");
            });

        order.getOrderItems().remove(itemToRemove);

        Double updatedTotalPrice = order.getOrderItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();

        order.setTotalPrice(updatedTotalPrice);

        orderRepository.save(order);
        
        log.info("Order item with an ID of {} removed successfully.", itemId);
    }
}
