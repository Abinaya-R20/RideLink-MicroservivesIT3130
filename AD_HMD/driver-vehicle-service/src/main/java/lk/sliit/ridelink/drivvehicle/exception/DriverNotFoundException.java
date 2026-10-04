package lk.sliit.ridelink.drivvehicle.exception;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(String driverId) {
        super("Driver not found with ID: " + driverId);
    }
}
