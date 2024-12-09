package com.example.fooddeliveryapp.OrderService.controllers;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.fooddeliveryapp.OrderService.OrderServiceConfig;
import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
import com.example.fooddeliveryapp.OrderService.entities.Order;
import com.example.fooddeliveryapp.OrderService.external.constants.PaymentMode;
import com.example.fooddeliveryapp.OrderService.models.OrderItemRequest;
import com.example.fooddeliveryapp.OrderService.models.OrderRequest;
import com.example.fooddeliveryapp.OrderService.repositories.OrderRepository;
import com.example.fooddeliveryapp.OrderService.services.IOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
public class OrderControllerTest {
	
	@Autowired
	IOrderService orderService;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@RegisterExtension
    static WireMockExtension wireMockServer
            = WireMockExtension.newInstance()
            .options(WireMockConfiguration
                    .wireMockConfig()
                    .port(8080))
            .build();
	
    private ObjectMapper objectMapper
    = new ObjectMapper()
    .findAndRegisterModules()
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	
	@Test
    public void test_WhenGetOrder_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        
        orderRepository.save(new Order(orderId, "user@gmail.com", UUID.randomUUID(), 100.0, OrderStatus.DELIVERED, LocalDateTime.now(), new ArrayList<>()));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/order/{id}", orderId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();

        Order order = orderRepository.findById(orderId).get();

        String expectedResponse = getOrderResponse(order);

        assertEquals(expectedResponse, actualResponse);
    }
	
	@Test
	public void test_WhenProcessOrder_Success() throws Exception {
	    OrderItemRequest itemRequest = OrderItemRequest.builder()
	            .dishId(UUID.randomUUID())
	            .quantity(2)
	            .build();

	    OrderRequest orderRequest = OrderRequest.builder()
	            .restaurantId(UUID.randomUUID())
	            .paymentMode(PaymentMode.CARD)
	            .items(Arrays.asList(itemRequest))
	            .build();

	    UUID createdOrderId = orderService.processOrder(orderRequest, "user@gmail.com");

	    Order savedOrder = orderRepository.findById(createdOrderId).orElseThrow();
	    assertEquals(createdOrderId, savedOrder.getOrderId());

	    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/process-order")
	            .with(jwt().authorities(new SimpleGrantedAuthority("Customer"))))
	            .andExpect(MockMvcResultMatchers.status().isCreated())
	            .andReturn();

	    String actualResponse = mvcResult.getResponse().getContentAsString();
	    UUID returnedOrderId = UUID.fromString(actualResponse);
	    assertEquals(returnedOrderId, savedOrder.getOrderId());
	}
	
	@Test
	public void test_WhenGetOrder_Unauthorized() throws Exception {
		UUID randomOrderId = UUID.randomUUID();
		
	    mockMvc.perform(MockMvcRequestBuilders.get("/order/" + randomOrderId)
	            .with(jwt().authorities(new SimpleGrantedAuthority("User")))
	            .contentType(MediaType.APPLICATION_JSON_VALUE))
	            .andExpect(MockMvcResultMatchers.status().isForbidden())
	            .andReturn();
	}
	
	@Test
	public void test_WhenGetOrder_NotFound() throws Exception {
		String invalidOrderId = "invalid-uuid-string";
		
	    mockMvc.perform(MockMvcRequestBuilders.get("/order/" + invalidOrderId)
	            .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
	            .contentType(MediaType.APPLICATION_JSON_VALUE))
	            .andExpect(MockMvcResultMatchers.status().isNotFound())
	            .andReturn();
	}
	
    private String getOrderResponse(Order order) throws JsonProcessingException {
    	 return objectMapper.writeValueAsString(order);
    }
    
    @Test
    public void test_WhenProcessOrder_Unauthorized() throws Exception {
        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .dishId(UUID.randomUUID())
                .quantity(2)
                .build();

        OrderRequest orderRequest = OrderRequest.builder()
                .restaurantId(UUID.randomUUID())
                .paymentMode(PaymentMode.CARD)
                .items(Arrays.asList(itemRequest))
                .build();

        // Perform the POST request without a non-authorized role
        mockMvc.perform(MockMvcRequestBuilders.post("/process-order")
                .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }
}
