package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.model.Receipt;
import com.ridelink.farepayment.service.ReceiptService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/ride/{rideId}")
    public Receipt generateReceipt(@PathVariable String rideId) {
        return receiptService.generateReceipt(rideId);
    }

    @GetMapping("/ride/{rideId}")
    public Receipt getReceipt(@PathVariable String rideId) {
        return receiptService.getReceiptByRideId(rideId);
    }
}