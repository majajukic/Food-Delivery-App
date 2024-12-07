package com.example.fooddeliveryapp.OrderService.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.fooddeliveryapp.OrderService.models.OrderRequest;
import com.example.fooddeliveryapp.OrderService.models.OrderResponse;
import com.example.fooddeliveryapp.OrderService.services.IOrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {
	@Autowired
	private IOrderService orderService;
	
	 /**
     * Retrieves the details of a specific order by its ID.
     * This method fetches the order details, including associated items and other metadata, 
     * from the database and returns them in the response.
     * Both users who have the role of Admin and the role of Customer can view order details
     * 
     * @param orderId - The unique identifier of the order to retrieve.
     * @return A response containing the details of the requested order and an OK status (200).
     */
	@PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable("id") UUID orderId) {
		OrderResponse orderResponse = orderService.getOrderDetails(orderId);
		
		return new ResponseEntity<>(orderResponse, HttpStatus.OK);
	}
	
	/**
	 * Places a new order for a user.
	 * This method receives an order request object in the request body, 
	 * creates an order entity along with its items in the database, 
	 * and returns the order ID.
	 * Only a user with a role of Customer can initiate the ordering process
	 * 
	 * @param orderRequest - The data needed to place a new order, including user and restaurant details, 
	 *                       as well as the items included in the order.
	 * @return A response containing the ID of the newly created order and a CREATED status (201).
	 */
	@PreAuthorize("hasAuthority('Customer')")
	@PostMapping("/process-order")
	public ResponseEntity<UUID> processOrder(@RequestBody @Valid OrderRequest orderRequest) {
		UUID orderId = orderService.processOrder(orderRequest);
		
		return new ResponseEntity<>(orderId, HttpStatus.CREATED);
	}
}
