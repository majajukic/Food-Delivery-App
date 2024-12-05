package com.example.fooddeliveryapp.OrderService.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
import com.example.fooddeliveryapp.OrderService.entities.Order;
import com.example.fooddeliveryapp.OrderService.entities.OrderItem;
import com.example.fooddeliveryapp.OrderService.exceptions.EmptyOrderException;
import com.example.fooddeliveryapp.OrderService.exceptions.OrderNotFoundException;
import com.example.fooddeliveryapp.OrderService.external.clients.IRestaurantService;
import com.example.fooddeliveryapp.OrderService.external.exceptions.DishNotAvailableException;
import com.example.fooddeliveryapp.OrderService.external.exceptions.DishNotFoundException;
import com.example.fooddeliveryapp.OrderService.external.models.DishResponse;
import com.example.fooddeliveryapp.OrderService.models.OrderRequest;
import com.example.fooddeliveryapp.OrderService.repositories.OrderRepository;

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

	private final OrderRepository orderRepository;
	private final IRestaurantService restaurantService;
	
	@Autowired
	public OrderService(OrderRepository orderRepository, IRestaurantService restaurantService) {
		this.orderRepository = orderRepository;
		this.restaurantService = restaurantService;
	}
	
	/**
	 * Processes entire order workflow.
	 * This method receives an order request object containing the user's details,
	 * restaurant information, and items in the order. It performs the following actions:
	 * 
	 * 1. Checks if the order contains valid items (at least one item must be included).
	 * 2. Calculates the total price of the order based on the items and their quantities.
	 * 3. Processes payment for the order.
	 * 4. Creates a new order and its associated order items in the database.
	 * 5. Initiates the delivery process (which will asynchronously update order status to DELIVERED when done).
	 * 
	 * Communication with other services:
	 * - Checking if the dishes in the order are available in the corresponding restaurant (Restaurant service).
	 * - Integrate payment processing, including support for different payment methods (Payment service).
	 * - Upon successful payment, update the order status to "DELIVERING" and initiate the delivery process (Delivery service).
	 * - If the delivery process is completed, update the order status to "DELIVERED" (Async call from Delivery Service back to Order service).
	 * 
	 * @param orderRequest - The data needed to create a new order, including user and restaurant details, 
	 *                       as well as the items in the order.
	 * @return The ID of the newly created order.
	 * @throws EmptyOrderException if the order has no order items.
	 */
	@Override
	public UUID processOrder(@Valid OrderRequest orderRequest) {
	    log.info("Processing the order...");

	    List<OrderItem> orderItems = validateAndPrepareOrderItems(orderRequest);

	    double totalPrice = calculateTotalPrice(orderItems);

	    processPayment(orderRequest, totalPrice);

	    Order order = saveOrder(orderRequest, orderItems, totalPrice);

	    initiateDelivery(order);

	    log.info("Order processed and delivered successfully.");
	    
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
	
	private List<OrderItem> validateAndPrepareOrderItems(OrderRequest orderRequest) {
	    if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
	        log.error("Cannot save an order with no items.");
	        throw new EmptyOrderException("Order must have at least one item.");
	    }

	    return orderRequest.getItems().stream().map(item -> {
	        ResponseEntity<DishResponse> response = restaurantService.getDishById(item.getDishId());
	        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
	        	log.error("Dish with an ID of {} was not found.", item.getDishId());
	            throw new DishNotFoundException("Dish with ID " + item.getDishId() + " not found.");
	        }

	        DishResponse dishResponse = response.getBody();
	        if (!dishResponse.getAvailability()) {
	        	log.error("Dish with an ID of {} is not available.", item.getDishId());
	            throw new DishNotAvailableException("Dish with ID " + item.getDishId() + " is not available.");
	        }

	        OrderItem orderItem = new OrderItem();
	        orderItem.setDishId(item.getDishId());
	        orderItem.setQuantity(item.getQuantity());
	        orderItem.setPrice(dishResponse.getPrice());
	        return orderItem;
	    }).toList();
	}
	
	// ========================== Helper Methods ==========================
	
	private double calculateTotalPrice(List<OrderItem> orderItems) {
	    return orderItems.stream()
	            .mapToDouble(item -> item.getPrice() * item.getQuantity())
	            .sum();
	}
	
	private void processPayment(OrderRequest orderRequest, double totalPrice) {
		log.info("Processing payment...");
	    /*PaymentRequest paymentRequest = new PaymentRequest(orderRequest.getUserId(), totalPrice, orderRequest.getPaymentDetails());
	    ResponseEntity<PaymentResponse> paymentResponse = paymentService.processPayment(paymentRequest);

	    if (!paymentResponse.getStatusCode().is2xxSuccessful() || !paymentResponse.getBody().isPaymentSuccessful()) {
	        log.error("Payment failed for order.");
	        throw new PaymentException("Payment processing failed.");
	    }*/
	}
	
	private Order saveOrder(OrderRequest orderRequest, List<OrderItem> orderItems, double totalPrice) {
	    Order order = new Order();
	    order.setUserId(orderRequest.getUserId());
	    order.setRestaurantId(orderRequest.getRestaurantId());
	    order.setTotalPrice(totalPrice);
	    order.setStatus(OrderStatus.PLACED);
	    order.setTimestamp(LocalDateTime.now());
	    order.setOrderItems(orderItems);

	    orderItems.forEach(item -> item.setOrder(order)); 
	    
	    orderRepository.save(order);
	    
	    return order;
	}
	
	private void initiateDelivery(Order order) {
		log.info("Initiating delivery process...");
	    /*DeliveryRequest deliveryRequest = new DeliveryRequest(order.getOrderId(), order.getUserId(), order.getRestaurantId(), order.getOrderItems());
	    ResponseEntity<DeliveryResponse> deliveryResponse = deliveryService.initiateDelivery(deliveryRequest);

	    if (!deliveryResponse.getStatusCode().is2xxSuccessful() || !deliveryResponse.getBody().isDeliveryInitiated()) {
	        log.error("Failed to initiate delivery for order: " + order.getOrderId());
	        throw new DeliveryException("Delivery initiation failed.");
	    }*/
	}
}
