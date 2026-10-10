package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.repository.FareRepository;
import org.springframework.web.bind.annotation.*;
import com.ridelink.farepayment.service.FareService;

import java.util.List;

@RestController
@RequestMapping("/api/fares")
public class FareController {

    private final FareRepository fareRepository;
    private final FareService fareService;

    public FareController(FareRepository fareRepository, FareService fareService) {
    this.fareRepository = fareRepository;
    this.fareService = fareService;
}

    @PostMapping
public Fare createFare(@RequestBody Fare fare) {
    return fareService.createFare(fare);
}

    @GetMapping
    public List<Fare> getAllFares() {
        return fareRepository.findAll();
    }
    @GetMapping("/estimate")
public double estimateFare(@RequestParam double distanceKm) {
    return fareService.calculateEstimatedFare(distanceKm);
}
@GetMapping("/final")
public double calculateFinalFare(@RequestParam double distanceKm) {
    return fareService.calculateFinalFare(distanceKm);
}

@PutMapping("/finalize/{rideId}")
public Fare finalizeFare(
        @PathVariable String rideId,
        @RequestParam double distanceKm) {

    return fareService.finalizeFare(rideId, distanceKm);
}

}