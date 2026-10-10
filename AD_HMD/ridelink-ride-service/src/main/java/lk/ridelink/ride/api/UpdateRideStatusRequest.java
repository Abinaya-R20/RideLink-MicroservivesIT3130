package lk.ridelink.ride.api;

import jakarta.validation.constraints.NotNull;
import lk.ridelink.ride.model.RideStatus;

public record UpdateRideStatusRequest(@NotNull RideStatus status) {}
