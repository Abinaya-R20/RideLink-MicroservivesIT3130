package lk.ridelink.ride.service;

import lk.ridelink.ride.api.*;
import lk.ridelink.ride.driver.*;
import lk.ridelink.ride.error.ApiException;
import lk.ridelink.ride.model.*;
import lk.ridelink.ride.repository.RideRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;
import java.util.List;

@Service
public class RideService {
    private final RideRepository repository;
    private final DriverClient driverClient;

    public RideService(RideRepository repository, DriverClient driverClient) {
        this.repository = repository;
        this.driverClient = driverClient;
    }

    @Transactional
    public RideResponse create(CreateRideRequest request, String passengerId) {
        List<DriverCandidate> candidates;
        try {
            candidates = driverClient.availableDrivers();
        } catch (RestClientResponseException ex) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "DRIVER_SERVICE_UNAVAILABLE",
                "Could not retrieve available drivers");
        }
        if (candidates.isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT, "NO_AVAILABLE_DRIVER", "No available driver was found");
        }

        for (DriverCandidate candidate : candidates) {
            if (candidate.driverId() == null || candidate.driverId().isBlank()) continue;
            try {
                // A 409 means another request already took this driver: continue to next candidate.
                driverClient.setDriverStatus(candidate.driverId(), "BUSY");
                Ride ride = new Ride(passengerId, candidate.driverId(), request.pickup().trim(), request.destination().trim());
                return RideResponse.from(repository.save(ride));
            } catch (RestClientResponseException ex) {
                if (ex.getStatusCode().value() == 409) continue;
                throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "DRIVER_SERVICE_UNAVAILABLE",
                    "Driver Service could not reserve a driver");
            }
        }
        throw new ApiException(HttpStatus.CONFLICT, "NO_AVAILABLE_DRIVER",
            "All available drivers were taken; please request again");
    }

    @Transactional(readOnly = true)
    public RideResponse get(String rideId, Authentication auth) {
        Ride ride = find(rideId);
        ensureCanView(ride, auth);
        return RideResponse.from(ride);
    }

    @Transactional(readOnly = true)
    public List<RideResponse> mine(Authentication auth) {
        String role = role(auth);
        if (role.equals("PASSENGER"))
            return repository.findByPassengerIdOrderByCreatedAtDesc(auth.getName()).stream().map(RideResponse::from).toList();
        if (role.equals("DRIVER"))
            return repository.findByDriverIdOrderByCreatedAtDesc(auth.getName()).stream().map(RideResponse::from).toList();
        return repository.findAll().stream().map(RideResponse::from).toList();
    }

    @Transactional
    public RideResponse updateStatus(String rideId, RideStatus next, Authentication auth) {
        Ride ride = find(rideId);
        String role = role(auth);
        RideStatus current = ride.getStatus();

        if (next == RideStatus.CANCELLED) {
            boolean passengerOwner = role.equals("PASSENGER") && ride.getPassengerId().equals(auth.getName());
            boolean assignedDriver = role.equals("DRIVER") && auth.getName().equals(ride.getDriverId());
            if (!(passengerOwner || assignedDriver || role.equals("ADMIN")))
                throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "You cannot cancel this ride");
            if (!(current == RideStatus.ASSIGNED || current == RideStatus.ACCEPTED))
                throw new ApiException(HttpStatus.CONFLICT, "INVALID_STATUS_TRANSITION", "Only assigned or accepted rides can be cancelled");
            ride.changeStatus(RideStatus.CANCELLED);
            releaseDriver(ride.getDriverId());
            return RideResponse.from(repository.save(ride));
        }

        if (!role.equals("DRIVER") && !role.equals("ADMIN"))
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Only the assigned driver can update ride progress");
        if (role.equals("DRIVER") && !auth.getName().equals(ride.getDriverId()))
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "This ride is assigned to another driver");

        boolean valid = (current == RideStatus.ASSIGNED && next == RideStatus.ACCEPTED)
            || (current == RideStatus.ACCEPTED && next == RideStatus.IN_PROGRESS)
            || (current == RideStatus.IN_PROGRESS && next == RideStatus.COMPLETED);
        if (!valid)
            throw new ApiException(HttpStatus.CONFLICT, "INVALID_STATUS_TRANSITION",
                "Allowed progress: ASSIGNED -> ACCEPTED -> IN_PROGRESS -> COMPLETED");
        ride.changeStatus(next);
        if (next == RideStatus.COMPLETED) releaseDriver(ride.getDriverId());
        return RideResponse.from(repository.save(ride));
    }

    private void releaseDriver(String driverId) {
        if (driverId == null) return;
        try { driverClient.setDriverStatus(driverId, "AVAILABLE"); }
        catch (Exception ignored) { /* Keep ride lifecycle saved; log/monitor in production. */ }
    }

    private Ride find(String id) {
        return repository.findById(id).orElseThrow(() ->
            new ApiException(HttpStatus.NOT_FOUND, "RIDE_NOT_FOUND", "Ride not found"));
    }
    private void ensureCanView(Ride ride, Authentication auth) {
        String role = role(auth);
        if (role.equals("ADMIN")) return;
        if (role.equals("PASSENGER") && ride.getPassengerId().equals(auth.getName())) return;
        if (role.equals("DRIVER") && auth.getName().equals(ride.getDriverId())) return;
        throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "You cannot view this ride");
    }
    private String role(Authentication auth) {
        return auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority().replace("ROLE_", "")).orElse("");
    }
}
