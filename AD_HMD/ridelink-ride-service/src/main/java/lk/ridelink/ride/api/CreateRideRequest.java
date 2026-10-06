package lk.ridelink.ride.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRideRequest(
    @NotBlank @Size(max = 180) String pickup,
    @NotBlank @Size(max = 180) String destination
) {}
