package com.ridelink.farepayment.service;

import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.Receipt;
import com.ridelink.farepayment.repository.PaymentRepository;
import com.ridelink.farepayment.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final PaymentRepository paymentRepository;

    public ReceiptService(
            ReceiptRepository receiptRepository,
            PaymentRepository paymentRepository) {

        this.receiptRepository = receiptRepository;
        this.paymentRepository = paymentRepository;
    }

    public Receipt generateReceipt(String rideId) {

        // Prevent duplicate receipt
        Receipt existingReceipt = receiptRepository.findByRideId(rideId);

        if (existingReceipt != null) {
            throw new IllegalArgumentException(
                    "Receipt already exists for ride: " + rideId
            );
        }

        // Find payment
        Payment payment = paymentRepository.findByRideId(rideId);

        if (payment == null) {
            throw new ResourceNotFoundException(
                    "Payment not found for ride: " + rideId
            );
        }

        // Generate receipt
        Receipt receipt = new Receipt();
        receipt.setRideId(payment.getRideId());
        receipt.setPaymentId(payment.getId());
        receipt.setAmount(payment.getAmount());
        receipt.setPaymentMethod(payment.getPaymentMethod());
        receipt.setIssuedAt(LocalDateTime.now());

        return receiptRepository.save(receipt);
    }

    public Receipt getReceiptByRideId(String rideId) {

        Receipt receipt = receiptRepository.findByRideId(rideId);

        if (receipt == null) {
            throw new ResourceNotFoundException(
                    "Receipt not found for ride: " + rideId
            );
        }

        return receipt;
    }
}