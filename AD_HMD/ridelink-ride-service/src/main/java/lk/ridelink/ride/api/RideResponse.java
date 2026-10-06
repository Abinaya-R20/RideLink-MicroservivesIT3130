package lk.ridelink.ride.api;

import lk.ridelink.ride.model.Ride;
import lk.ridelink.ride.model.RideStatus;
import java.time.Instant;

public record RideResponse(String id, String passengerId, String driverId, String pickup,
                           String destination, RideStatus status, Instant createdAt, Instant updatedAt) {
    public static RideResponse from(Ride ride) {
        return new RideResponse(ride.getId(), ride.getPassengerId(), ride.getDriverId(),
            ride.getPickup(), ride.getDestination(), ride.getStatus(), ride.getCreatedAt(), ride.getUpdatedAt());
    }
}
