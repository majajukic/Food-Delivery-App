package com.fooddeliveryservice.PaymentService.services;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


import com.fooddeliveryservice.PaymentService.constants.PaymentMode;
import com.fooddeliveryservice.PaymentService.constants.PaymentStatus;
import com.fooddeliveryservice.PaymentService.entities.PaymentDetails;
import com.fooddeliveryservice.PaymentService.exceptions.PaymentNotFoundException;
import com.fooddeliveryservice.PaymentService.models.PaymentRequest;
import com.fooddeliveryservice.PaymentService.models.PaymentResponse;
import com.fooddeliveryservice.PaymentService.repositories.PaymentRepository;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID orderId;
    private PaymentDetails paymentDetails;
    private PaymentRequest paymentRequest;

    @Before
    public void setUp() {
        orderId = UUID.randomUUID();
        paymentDetails = PaymentDetails.builder()
                .paymentId(UUID.randomUUID())
                .orderId(orderId)
                .amount(100.0)
                .paymentMode(PaymentMode.CARD)
                .status(PaymentStatus.SUCCESSFUL)
                .timestamp(LocalDateTime.now())
                .build();

        paymentRequest = PaymentRequest.builder()
                .orderId(orderId)
                .amount(100.0)
                .paymentMode(PaymentMode.CARD)
                .build();
    }

    @DisplayName("Get Payment Details by Order ID - Success Scenario")
    @Test
    public void test_When_Get_Payment_Details_By_OrderId_Success() {
        Mockito.when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(paymentDetails));

        PaymentResponse result = paymentService.getPaymentDetailsByOrderId(orderId);

        assertNotNull(result);
        assertEquals(paymentDetails.getPaymentId(), result.getPaymentId());
        assertEquals(paymentDetails.getPaymentMode(), result.getPaymentMode());
        assertEquals(paymentDetails.getStatus(), result.getStatus());
        assertEquals(paymentDetails.getTimestamp(), result.getPayedOn());

        Mockito.verify(paymentRepository, Mockito.times(1)).findByOrderId(orderId);
    }

    @DisplayName("Get Payment Details by Order ID - Failure Scenario")
    @Test
    public void test_When_Get_Payment_Details_By_OrderId_Payment_Not_Found() {
        Mockito.when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class,
                () -> paymentService.getPaymentDetailsByOrderId(orderId));

        assertEquals("Payment with an order ID of " + orderId + " not found", exception.getMessage());

        Mockito.verify(paymentRepository, Mockito.times(1)).findByOrderId(orderId);
    }

    @DisplayName("Create Payment - Success Scenario")
    @Test
    public void test_When_Create_Payment_Success() {
        Mockito.when(paymentRepository.save(Mockito.any(PaymentDetails.class))).thenReturn(paymentDetails);

        UUID paymentId = paymentService.createPayment(paymentRequest);

        assertNotNull(paymentId);
        assertEquals(paymentDetails.getPaymentId(), paymentId);

        Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(PaymentDetails.class));
    }
}
