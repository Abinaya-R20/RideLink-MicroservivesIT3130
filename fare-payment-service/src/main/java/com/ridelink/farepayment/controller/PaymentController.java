package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment recordPayment(@RequestBody Payment payment) {
        return paymentService.recordPayment(payment);
    }
    @GetMapping("/ride/{rideId}")
public Payment getPaymentByRideId(@PathVariable String rideId) {
    return paymentService.getPaymentByRideId(rideId);
}
}