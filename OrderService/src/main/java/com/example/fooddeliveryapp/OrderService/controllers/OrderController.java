package com.example.fooddeliveryapp.OrderService.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
import com.example.fooddeliveryapp.OrderService.models.OrderRequest;
import com.example.fooddeliveryapp.OrderService.services.IOrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {
	@Autowired
	private IOrderService orderService;
	
	/**
	 * Places a new order for a user.
	 * This method receives an order request object in the request body, 
	 * creates an order entity along with its items in the database, 
	 * and returns the order ID.
	 * 
	 * @param orderRequest - The data needed to place a new order, including user and restaurant details, 
	 *                       as well as the items included in the order.
	 * @return A response containing the ID of the newly created order and a CREATED status (201).
	 */
	@PostMapping("/place-order")
	public ResponseEntity<UUID> placeOrder(@RequestBody @Valid OrderRequest orderRequest) {
		UUID orderId = orderService.placeOrder(orderRequest);
		
		return new ResponseEntity<>(orderId, HttpStatus.CREATED);
	}
	
	 /**
     * Updates the status of an existing order.
     * This method receives the order ID and the new status, and it updates the 
     * order's status in the database.
     * 
     * @param orderId - The ID of the order whose status needs to be updated.
     * @param newStatus - The new status to set for the order.
     * @return A response indicating whether the update was successful or not.
     */
    @PatchMapping("/{id}/update-status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable("id") UUID orderId, @RequestBody OrderStatus newStatus) {
        orderService.updateOrderStatus(orderId, newStatus);
        
        return new ResponseEntity<>(HttpStatus.OK); 
    }
	
	/**
     * Removes an item from the order and updates the total price.
     * 
     * @param orderId - The ID of the order from which the item is to be removed.
     * @param itemId - The ID of the item to remove from the order.
     * @return A response with a NO_CONTENT status (204) indicating successful deletion.
     */
    @DeleteMapping("/{id}/remove-item/{itemId}")
    public ResponseEntity<Void> removeItemFromOrder(@PathVariable("id") UUID orderId, @PathVariable UUID itemId) {
        orderService.removeItemFromOrder(orderId, itemId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    }
    
    // to-do: add endpoints for increasing/decreasing quantity of items in an order and for getting order details when communication with Restaurant service is implemented
}
