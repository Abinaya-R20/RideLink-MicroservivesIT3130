package com.ridelink.farepayment;

import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.repository.FareRepository;
import com.ridelink.farepayment.repository.PaymentRepository;
import com.ridelink.farepayment.service.PaymentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentRepository paymentRepository;
    private FareRepository fareRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        fareRepository = mock(FareRepository.class);

        paymentService = new PaymentService(
                paymentRepository,
                fareRepository
        );
    }

    @Test
    void shouldRecordPaymentUsingFinalizedFare() {

        Fare fare = new Fare();
        fare.setRideId("RIDE-001");
        fare.setFinalFare(1200.0);
        fare.setStatus("FINALIZED");

        Payment payment = new Payment();
        payment.setRideId("RIDE-001");
        payment.setPaymentMethod("CARD");

        when(fareRepository.findByRideId("RIDE-001"))
                .thenReturn(fare);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.recordPayment(payment);

        assertEquals(1200.0, result.getAmount());
        assertEquals("PAID", result.getPaymentStatus());
        assertEquals("CARD", result.getPaymentMethod());

        verify(paymentRepository).save(payment);
    }

    @Test
    void shouldRejectPaymentWhenFareNotFinalized() {

        Fare fare = new Fare();
        fare.setRideId("RIDE-002");
        fare.setStatus("ESTIMATED");

        when(fareRepository.findByRideId("RIDE-002"))
                .thenReturn(fare);

        Payment payment = new Payment();
        payment.setRideId("RIDE-002");

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.recordPayment(payment)
        );
    }

    @Test
    void shouldRejectPaymentWhenFareNotFound() {

        when(fareRepository.findByRideId("RIDE-999"))
                .thenReturn(null);

        Payment payment = new Payment();
        payment.setRideId("RIDE-999");

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.recordPayment(payment)
        );
    }

    @Test
    void shouldGetPaymentByRideId() {

        Payment payment = new Payment();
        payment.setRideId("RIDE-001");
        payment.setPaymentStatus("PAID");

        when(paymentRepository.findByRideId("RIDE-001"))
                .thenReturn(payment);

        Payment result =
                paymentService.getPaymentByRideId("RIDE-001");

        assertEquals("RIDE-001", result.getRideId());
        assertEquals("PAID", result.getPaymentStatus());
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {

        when(paymentRepository.findByRideId("RIDE-999"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.getPaymentByRideId("RIDE-999")
        );
    }
    @Test
void shouldRejectDuplicatePayment() {

    Payment existingPayment = new Payment();
    existingPayment.setRideId("RIDE-001");
    existingPayment.setPaymentStatus("PAID");

    when(paymentRepository.findByRideId("RIDE-001"))
            .thenReturn(existingPayment);

    Payment newPayment = new Payment();
    newPayment.setRideId("RIDE-001");
    newPayment.setPaymentMethod("CARD");

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.recordPayment(newPayment)
    );

    assertEquals(
            "Payment already exists for ride: RIDE-001",
            exception.getMessage()
    );

    verify(paymentRepository, never())
            .save(any(Payment.class));
}
}