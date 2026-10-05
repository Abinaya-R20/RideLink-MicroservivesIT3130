package com.ridelink.farepayment.service;

import org.springframework.stereotype.Service;
import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.repository.FareRepository;
import com.ridelink.farepayment.exception.ResourceNotFoundException;

@Service
public class FareService {

    private final FareRepository fareRepository;

public FareService(FareRepository fareRepository) {
    this.fareRepository = fareRepository;
}

    private static final double BASE_FARE = 200.0;
    private static final double RATE_PER_KM = 100.0;

    public double calculateEstimatedFare(double distanceKm) {

    if (distanceKm <= 0) {
        throw new IllegalArgumentException("Distance must be greater than 0");
    }

    return BASE_FARE + (distanceKm * RATE_PER_KM);
}
   public double calculateFinalFare(double distanceKm) {

    if (distanceKm <= 0) {
        throw new IllegalArgumentException("Distance must be greater than 0");
    }

    return BASE_FARE + (distanceKm * RATE_PER_KM);
}

public Fare createFare(Fare fare) {

    double estimatedFare =
            calculateEstimatedFare(fare.getDistanceKm());

    fare.setEstimatedFare(estimatedFare);
    fare.setFinalFare(0.0);
    fare.setStatus("ESTIMATED");

    return fareRepository.save(fare);
}
public Fare finalizeFare(String rideId, double distanceKm) {

    Fare fare = fareRepository.findByRideId(rideId);

    if (fare == null) {
    throw new ResourceNotFoundException("Fare not found for ride: " + rideId);
}

    double finalFare = calculateFinalFare(distanceKm);

    fare.setDistanceKm(distanceKm);
    fare.setFinalFare(finalFare);
    fare.setStatus("FINALIZED");

    return fareRepository.save(fare);
}

}