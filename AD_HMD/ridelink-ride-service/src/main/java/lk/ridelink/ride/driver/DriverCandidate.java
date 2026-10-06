package lk.ridelink.ride.driver;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DriverCandidate(@JsonAlias({"id", "driver_id"}) String driverId) {}
