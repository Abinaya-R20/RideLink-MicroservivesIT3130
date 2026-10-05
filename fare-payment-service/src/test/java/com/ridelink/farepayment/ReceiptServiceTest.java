package com.ridelink.farepayment;

import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.Receipt;
import com.ridelink.farepayment.repository.PaymentRepository;
import com.ridelink.farepayment.repository.ReceiptRepository;
import com.ridelink.farepayment.service.ReceiptService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReceiptServiceTest {

    private ReceiptRepository receiptRepository;
    private PaymentRepository paymentRepository;
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        receiptRepository = mock(ReceiptRepository.class);
        paymentRepository = mock(PaymentRepository.class);

        receiptService = new ReceiptService(
                receiptRepository,
                paymentRepository
        );
    }

    @Test
    void shouldGenerateReceiptFromPayment() {

        Payment payment = new Payment();
        payment.setId("PAY-001");
        payment.setRideId("RIDE-001");
        payment.setAmount(1200.0);
        payment.setPaymentMethod("CARD");
        payment.setPaymentStatus("PAID");

        when(paymentRepository.findByRideId("RIDE-001"))
                .thenReturn(payment);

        when(receiptRepository.save(any(Receipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Receipt result = receiptService.generateReceipt("RIDE-001");

        assertEquals("RIDE-001", result.getRideId());
        assertEquals("PAY-001", result.getPaymentId());
        assertEquals(1200.0, result.getAmount());
        assertEquals("CARD", result.getPaymentMethod());
        assertNotNull(result.getIssuedAt());

        verify(receiptRepository).save(any(Receipt.class));
    }

    @Test
    void shouldGetReceiptByRideId() {

        Receipt receipt = new Receipt();
        receipt.setRideId("RIDE-001");
        receipt.setAmount(1200.0);

        when(receiptRepository.findByRideId("RIDE-001"))
                .thenReturn(receipt);

        Receipt result =
                receiptService.getReceiptByRideId("RIDE-001");

        assertEquals("RIDE-001", result.getRideId());
        assertEquals(1200.0, result.getAmount());
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {

        when(paymentRepository.findByRideId("RIDE-999"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> receiptService.generateReceipt("RIDE-999")
        );
    }

    @Test
    void shouldThrowExceptionWhenReceiptNotFound() {

        when(receiptRepository.findByRideId("RIDE-999"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> receiptService.getReceiptByRideId("RIDE-999")
        );
    }
    @Test
void shouldRejectDuplicateReceipt() {

    Receipt existingReceipt = new Receipt();
    existingReceipt.setRideId("RIDE-001");

    when(receiptRepository.findByRideId("RIDE-001"))
            .thenReturn(existingReceipt);

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> receiptService.generateReceipt("RIDE-001")
    );

    assertEquals(
            "Receipt already exists for ride: RIDE-001",
            exception.getMessage()
    );

    verify(receiptRepository, never())
            .save(any(Receipt.class));
}
}