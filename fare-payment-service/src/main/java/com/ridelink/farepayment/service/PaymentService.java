package com.ridelink.farepayment.service;

import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.repository.FareRepository;
import com.ridelink.farepayment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FareRepository fareRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            FareRepository fareRepository) {

        this.paymentRepository = paymentRepository;
        this.fareRepository = fareRepository;
    }

    public Payment recordPayment(Payment payment) {

    // Check whether a payment already exists for this ride
    Payment existingPayment =
            paymentRepository.findByRideId(payment.getRideId());

    if (existingPayment != null) {
        throw new IllegalArgumentException(
                "Payment already exists for ride: " + payment.getRideId()
        );
    }

    // Find the finalized fare
    Fare fare = fareRepository.findByRideId(payment.getRideId());

    if (fare == null) {
        throw new ResourceNotFoundException(
                "Fare not found for ride: " + payment.getRideId()
        );
    }

    if (!"FINALIZED".equals(fare.getStatus())) {
        throw new IllegalArgumentException(
                "Fare must be finalized before payment"
        );
    }

    // Amount comes from the finalized fare
    payment.setAmount(fare.getFinalFare());
    payment.setPaymentStatus("PAID");

    return paymentRepository.save(payment);
}

    public Payment getPaymentByRideId(String rideId) {

        Payment payment = paymentRepository.findByRideId(rideId);

        if (payment == null) {
            throw new ResourceNotFoundException(
                    "Payment not found for ride: " + rideId
            );
        }

        return payment;
    }
}