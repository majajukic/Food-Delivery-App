package com.example.fooddeliveryapp.OrderService.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
import com.example.fooddeliveryapp.OrderService.entities.Order;
import com.example.fooddeliveryapp.OrderService.entities.OrderItem;
import com.example.fooddeliveryapp.OrderService.exceptions.EmptyOrderException;
import com.example.fooddeliveryapp.OrderService.exceptions.OrderNotFoundException;
import com.example.fooddeliveryapp.OrderService.external.clients.IDeliveryService;
import com.example.fooddeliveryapp.OrderService.external.clients.IPaymentService;
import com.example.fooddeliveryapp.OrderService.external.clients.IRestaurantService;
import com.example.fooddeliveryapp.OrderService.external.constants.DeliveryStatus;
import com.example.fooddeliveryapp.OrderService.external.constants.PaymentMode;
import com.example.fooddeliveryapp.OrderService.external.constants.PaymentStatus;
import com.example.fooddeliveryapp.OrderService.external.exceptions.DishNotAvailableException;
import com.example.fooddeliveryapp.OrderService.external.exceptions.DishNotFoundException;
import com.example.fooddeliveryapp.OrderService.external.models.DeliveryRequest;
import com.example.fooddeliveryapp.OrderService.external.models.DeliveryResponse;
import com.example.fooddeliveryapp.OrderService.external.models.DishResponse;
import com.example.fooddeliveryapp.OrderService.external.models.PaymentRequest;
import com.example.fooddeliveryapp.OrderService.external.models.PaymentResponse;
import com.example.fooddeliveryapp.OrderService.models.OrderItemRequest;
import com.example.fooddeliveryapp.OrderService.models.OrderRequest;
import com.example.fooddeliveryapp.OrderService.models.OrderResponse;
import com.example.fooddeliveryapp.OrderService.repositories.OrderRepository;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {
	@Mock
	private OrderRepository orderRepository;
	
	@Mock
	private IRestaurantService restaurantService;
	
	@Mock
	private IPaymentService paymentService;
	
	@Mock
	private IDeliveryService deliveryService;
	
	@InjectMocks
	IOrderService orderService = new OrderService();
	
    @DisplayName("Get Order - Success Scenario")
    @Test
    public void test_When_Get_Order_Success() {
        // Creating mock order and its related order items
        Order order = getMockOrder();
        Mockito.when(orderRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(order));
        
        // Mocking the external service calls with your updated response models
        Mockito.when(restaurantService.getDishById(Mockito.any(UUID.class)))
               .thenReturn(ResponseEntity.ok(new DishResponse(UUID.randomUUID(), "Dish Name", 150.0, "Description", true)));
        
        Mockito.when(paymentService.getPaymentDetailsByOrderId(Mockito.any(UUID.class)))
        	.thenReturn(ResponseEntity.ok(new PaymentResponse(UUID.randomUUID(), PaymentMode.CARD, PaymentStatus.SUCCESSFUL, LocalDateTime.now())));
        
        Mockito.when(deliveryService.getDeliveryDetailsByOrderId(Mockito.any(UUID.class)))
               .thenReturn(ResponseEntity.ok(new DeliveryResponse(UUID.randomUUID(), DeliveryStatus.DELIVERED, LocalDateTime.now().minusHours(1), LocalDateTime.now())));
        
        // Call the method being tested
        OrderResponse orderResponse = orderService.getOrderDetails(UUID.fromString("806308BB-7ABB-4666-A353-E3689677FE6D"));

        // Asserting how many times the orderRepository was called
        Mockito.verify(orderRepository, Mockito.times(1))
        		.findById(Mockito.any(UUID.class));
        
        // Asserting how many times the external services were called
        Mockito.verify(restaurantService, Mockito.times(2)) // we have 2 items in the test order so this service needs to be called twice
               .getDishById(Mockito.any(UUID.class));
        
        Mockito.verify(paymentService, Mockito.times(1))
               .getPaymentDetailsByOrderId(Mockito.any(UUID.class));
        
        Mockito.verify(deliveryService, Mockito.times(1))
               .getDeliveryDetailsByOrderId(Mockito.any(UUID.class));
        
        // Asserting that the orderResponse object is not null
        assertNotNull(orderResponse);
        
        // Asserting expected and actual values
        assertEquals(order.getOrderId(), orderResponse.getOrderId());
    }
    
    @DisplayName("Get Order - Failure Scenario")
    @Test
    public void test_When_Get_Order_Not_Found() {
    	Mockito.when(orderRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.ofNullable(null));
    	
    	// Aserting the thrown exception
    	OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, 
    			() -> orderService.getOrderDetails(UUID.fromString("806308bb-7abb-4666-a353-e3689677fe6d")));
    	
    	// Asserting the exception message
        assertEquals("Order with an ID of 806308bb-7abb-4666-a353-e3689677fe6d not found", exception.getMessage());
        
        // Asserting how many times the orderRepository was called
        Mockito.verify(orderRepository, Mockito.times(1))
        		.findById(Mockito.any(UUID.class));
        
    }
    
    @DisplayName("Process Order - Success Scenario")
    @Test
    public void test_When_Process_Order_Success() {
        // Creating mock order and its related order items
        OrderRequest orderRequest = new OrderRequest(UUID.randomUUID(), PaymentMode.CARD, List.of(new OrderItemRequest(UUID.randomUUID(), 2)));
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(getMockOrderItem("04E22B5C-6BCC-4175-B6B7-5113A0A5D218", 2, 150.0));
        orderItems.add(getMockOrderItem("5AD007C8-53A4-401D-9EC3-A39BF63C583C", 1, 200.0));

        UUID orderId = UUID.randomUUID();

        // Mocking orderRepository.save to return an order with a valid orderId
        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
               .thenReturn(new Order(orderId, "user@example.com", orderRequest.getRestaurantId(), 300.0, OrderStatus.PLACED, LocalDateTime.now(), orderItems));

        // Mocking the external service calls
        Mockito.when(restaurantService.getDishById(Mockito.any(UUID.class)))
               .thenReturn(ResponseEntity.ok(new DishResponse(UUID.randomUUID(), "Dish Name", 150.0, "Description", true)));

        Mockito.when(paymentService.pay(Mockito.any(PaymentRequest.class)))
               .thenReturn(null);

        Mockito.when(deliveryService.initiateDelivery(Mockito.any(DeliveryRequest.class)))
               .thenReturn(null);

        // Call the method being tested
        UUID resultOrderId = orderService.processOrder(orderRequest, "user@example.com");

        // Asserting how many times the external services were called
        Mockito.verify(restaurantService, Mockito.times(1)).getDishById(Mockito.any(UUID.class)); 
        Mockito.verify(paymentService, Mockito.times(1)).pay(Mockito.any(PaymentRequest.class)); 
        Mockito.verify(deliveryService, Mockito.times(1)).initiateDelivery(Mockito.any(DeliveryRequest.class));

        // Asserting that the returned order ID is correct
        assertEquals(orderId, resultOrderId);
    }
    
    @DisplayName("Process Order - Failure Scenario - Empty Order Items")
    @Test
    public void test_When_Process_Order_Empty_Items_Failure() {
        // Creating an invalid order with no items
        OrderRequest orderRequest = new OrderRequest(UUID.randomUUID(), PaymentMode.CARD, List.of());
        
        // Asserting that an EmptyOrderException is thrown
        EmptyOrderException exception = assertThrows(EmptyOrderException.class, 
                () -> orderService.processOrder(orderRequest, "user@example.com"));
        
        // Asserting the exception message
        assertEquals("Order must have at least one item.", exception.getMessage());
    }
    
    @DisplayName("Process Order - Failure Scenario - Dish Not Found")
    @Test
    public void test_When_Process_Order_Dish_Not_Found_Failure() {
        // Creating order request with valid dish but mocked dish service to return null
        OrderRequest orderRequest = new OrderRequest(UUID.randomUUID(), PaymentMode.CARD, List.of(new OrderItemRequest(UUID.randomUUID(), 1)));
        
        // Mocking that the dish is not found
        Mockito.when(restaurantService.getDishById(Mockito.any(UUID.class)))
               .thenReturn(ResponseEntity.notFound().build()); // Simulating dish not found
        
        // Asserting that a DishNotFoundException is thrown
        DishNotFoundException exception = assertThrows(DishNotFoundException.class, 
                () -> orderService.processOrder(orderRequest, "user@example.com"));
        
        // Asserting the exception message
        assertEquals("Dish with ID " + orderRequest.getItems().get(0).getDishId() + " not found.", exception.getMessage());
    }
    
    @DisplayName("Process Order - Failure Scenario - Dish Not Available")
    @Test
    public void test_When_Process_Order_Dish_Not_Available_Failure() {
        // Creating order request with valid dish but mocked dish service to return unavailable dish
        OrderRequest orderRequest = new OrderRequest(UUID.randomUUID(), PaymentMode.CARD, List.of(new OrderItemRequest(UUID.randomUUID(), 1)));
        
        // Mocking that the dish is unavailable
        Mockito.when(restaurantService.getDishById(Mockito.any(UUID.class)))
               .thenReturn(ResponseEntity.ok(new DishResponse(UUID.randomUUID(), "Dish Name", 150.0, "Description", false))); // Dish not available
        
        // Asserting that a DishNotAvailableException is thrown
        DishNotAvailableException exception = assertThrows(DishNotAvailableException.class, 
                () -> orderService.processOrder(orderRequest, "user@example.com"));
        
        // Asserting the exception message
        assertEquals("Dish with ID " + orderRequest.getItems().get(0).getDishId() + " is not available.", exception.getMessage());
    }

	private Order getMockOrder() {
		List<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(getMockOrderItem("04E22B5C-6BCC-4175-B6B7-5113A0A5D218", 2, 150.0));
		orderItems.add(getMockOrderItem("5AD007C8-53A4-401D-9EC3-A39BF63C583C", 1, 200.0));
				
		return Order.builder()
				.orderId(UUID.fromString("806308BB-7ABB-4666-A353-E3689677FE6D"))
				.restaurantId(UUID.fromString("F9C71521-69BC-4D11-A2DE-274309A30354"))
				.userId("testuser1@gmail.com")
				.timestamp(LocalDateTime.now())
				.status(OrderStatus.DELIVERED)
				.totalPrice(670.0)
				.orderItems(orderItems)
				.build();
	}

	private OrderItem getMockOrderItem(String dishId, int quantity, double price) {
		return OrderItem.builder()
				.orderItemId(UUID.randomUUID())  
				.dishId(UUID.fromString(dishId))
				.quantity(quantity)
				.price(price) 
				.build();
	}
}
