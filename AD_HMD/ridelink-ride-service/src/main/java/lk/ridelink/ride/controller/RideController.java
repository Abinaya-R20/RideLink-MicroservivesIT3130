package lk.ridelink.ride.controller;

import jakarta.validation.Valid;
import lk.ridelink.ride.api.*;
import lk.ridelink.ride.model.RideStatus;
import lk.ridelink.ride.service.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rides")
public class RideController {
    private final RideService rideService;
    public RideController(RideService rideService) { this.rideService = rideService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public RideResponse create(@Valid @RequestBody CreateRideRequest request, Authentication auth) {
        return rideService.create(request, auth.getName());
    }

    @GetMapping("/my")
    public List<RideResponse> mine(Authentication auth) { return rideService.mine(auth); }

    @GetMapping("/{id}")
    public RideResponse get(@PathVariable String id, Authentication auth) {
        return rideService.get(id, auth);
    }

    @PatchMapping("/{id}/status")
    public RideResponse updateStatus(@PathVariable String id,
          @Valid @RequestBody UpdateRideStatusRequest request, Authentication auth) {
        return rideService.updateStatus(id, request.status(), auth);
    }
}
