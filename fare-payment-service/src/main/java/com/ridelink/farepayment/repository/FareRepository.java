package com.ridelink.farepayment.repository;

import com.ridelink.farepayment.model.Fare;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FareRepository extends MongoRepository<Fare, String> {

    Fare findByRideId(String rideId);
}