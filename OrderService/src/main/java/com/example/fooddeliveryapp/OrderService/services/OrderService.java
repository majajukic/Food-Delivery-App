package com.example.fooddeliveryapp.OrderService.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.example.fooddeliveryapp.OrderService.external.clients.IDeliveryService;
import com.example.fooddeliveryapp.OrderService.external.clients.IPaymentService;
import com.example.fooddeliveryapp.OrderService.external.clients.IRestaurantService;
import com.example.fooddeliveryapp.OrderService.external.exceptions.DishNotAvailableException;
import com.example.fooddeliveryapp.OrderService.external.exceptions.DishNotFoundException;
import com.example.fooddeliveryapp.OrderService.external.models.DeliveryRequest;
import com.example.fooddeliveryapp.OrderService.external.models.DeliveryResponse;
import com.example.fooddeliveryapp.OrderService.external.models.DishResponse;
import com.example.fooddeliveryapp.OrderService.external.models.PaymentRequest;
import com.example.fooddeliveryapp.OrderService.external.models.PaymentResponse;
import com.example.fooddeliveryapp.OrderService.models.OrderRequest;
import com.example.fooddeliveryapp.OrderService.models.OrderResponse;
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
	private final IPaymentService paymentService;
	private final IDeliveryService deliveryService;
	
	@Autowired
	public OrderService(OrderRepository orderRepository, IRestaurantService restaurantService, IPaymentService paymentService, IDeliveryService deliveryService) {
		this.orderRepository = orderRepository;
		this.restaurantService = restaurantService;
		this.paymentService = paymentService;
		this.deliveryService = deliveryService;
	}
	
    /**
     * Retrieves the details of a specific order by its ID.
     * 
     * This method fetches an order from the database, including its associated items, 
     * and other related information. It maps the retrieved order entity 
     * to an `OrderResponse` object, which is used as a DTO to send data to the client.
     * 
     * @param orderId - The unique identifier of the order to retrieve.
     * @return An `OrderResponse` object containing the details of the requested order.
     * @throws OrderNotFoundException if the order with the given ID does not exist.
     */
	@Override
	public OrderResponse getOrderDetails(UUID orderId) {
		log.info("Retrieving order with an ID of {} ...", orderId);
		
		Order order = orderRepository.findById(orderId)
	    		.orElseThrow(() -> {
	                log.error("Order with ID {} not found", orderId);
	                return new OrderNotFoundException("Order with an ID of " + orderId + " not found");
	            });
		
		// fetching dish details for each order item from restaurant service
		List<DishResponse> dishes = fetchDishesForOrderItems(order.getOrderItems());
		
		// fetching payment details from Payment service
		PaymentResponse paymentDetails = fetchPaymentDetailsByOrderId(order.getOrderId());
		
		DeliveryResponse deliveryDetails = fetchDeliveryDetailsByOrderId(order.getOrderId());
		
		OrderResponse orderResponse = OrderResponse.builder()
				.orderId(order.getOrderId())
				.status(order.getStatus())
				.amount(order.getTotalPrice())
				.createdAt(order.getTimestamp())
				.dishes(dishes)
				.paymentDetails(paymentDetails)
				.deliveryDetails(deliveryDetails)
				.build();
		
		log.info("Order with an ID of {} retireved successfully.", orderId);
		
		return orderResponse;
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

	    Order order = saveOrder(orderRequest, orderItems, totalPrice);
	    
	    boolean paymentSuccessful = processPayment(order, orderRequest);
	    
	    if (paymentSuccessful) {
	        initiateDelivery(order);
	    }

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

	    orderRepository.findById(orderId)
	    		.orElseThrow(() -> {
	                log.error("Order with ID {} not found", orderId);
	                return new OrderNotFoundException("Order with an ID of " + orderId + " not found");
	            });

	    orderRepository.updateOrderStatus(orderId, newStatus);

	    log.info("Status update for an order with an ID of {} successful", orderId);
	}
	
	// ========================== HELPER METHODS ==========================
	
	private List<DishResponse> fetchDishesForOrderItems(List<OrderItem> orderItems) {
	    List<DishResponse> dishDetails = new ArrayList<>();
	    
	    for (OrderItem item : orderItems) {
	        DishResponse dishResponse = restaurantService.getDishById(item.getDishId()).getBody();
	        
	        if (dishResponse != null) {
	            dishDetails.add(dishResponse);
	        } else {
	            log.error("Dish with ID {} not found for the order item.", item.getDishId());
	        }
	    }
	    
	    return dishDetails;
	}
	
	private PaymentResponse fetchPaymentDetailsByOrderId(UUID orderId) {
		PaymentResponse paymentResponse = paymentService.getPaymentDetailsByOrderId(orderId).getBody();
		
		return paymentResponse;
	}
	
	private DeliveryResponse fetchDeliveryDetailsByOrderId(UUID orderId) {
		DeliveryResponse deliveryResponse = deliveryService.getDeliveryDetailsByOrderId(orderId).getBody();
		
		return deliveryResponse;
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
	
	private double calculateTotalPrice(List<OrderItem> orderItems) {
	    return orderItems.stream()
	            .mapToDouble(item -> item.getPrice() * item.getQuantity())
	            .sum();
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
	
	private boolean processPayment(Order order, OrderRequest orderRequest) {
		log.info("Initiating payment process...");
	    PaymentRequest paymentRequest = PaymentRequest.builder()
	    		.orderId(order.getOrderId())
	    		.paymentMode(orderRequest.getPaymentMode())
	    		.amount(order.getTotalPrice())
	    		.build();
	    
	    try {
	        paymentService.pay(paymentRequest);
	        updateOrderStatus(order.getOrderId(), OrderStatus.PAYED);
	        log.info("Payment processed successfully for Order ID: {}", order.getOrderId());
	        return true; 
	    } catch (Exception ex) {
	        log.error("Error occurred in Payment service while processing payment. Error: {}", ex.getMessage());
	        updateOrderStatus(order.getOrderId(), OrderStatus.CANCELED);
	        return false;
	    }
	}
	
	private void initiateDelivery(Order order) {
		log.info("Initiating delivery process...");
		
		DeliveryRequest deliveryRequest = DeliveryRequest.builder()
		        .orderId(order.getOrderId())
		        .userId(order.getUserId())
		        .restaurantId(order.getRestaurantId())
		        .build();

	    try {
	        deliveryService.initiateDelivery(deliveryRequest);
	        updateOrderStatus(order.getOrderId(), OrderStatus.DELIVERING);
	    } catch (Exception ex) {
	        log.error("Failed to initiate delivery for order: {}. Error: {}", order.getOrderId(), ex.getMessage());
	    }
	}
}
