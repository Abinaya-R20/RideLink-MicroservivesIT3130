package lk.sliit.ridelink.drivvehicle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.sliit.ridelink.drivvehicle.dto.AvailabilityUpdateRequest;
import lk.sliit.ridelink.drivvehicle.dto.DriverCreateRequest;
import lk.sliit.ridelink.drivvehicle.dto.DriverResponse;
import lk.sliit.ridelink.drivvehicle.dto.DriverUpdateRequest;
import lk.sliit.ridelink.drivvehicle.dto.LocationResponse;
import lk.sliit.ridelink.drivvehicle.dto.LocationUpdateRequest;
import lk.sliit.ridelink.drivvehicle.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Management", description = "APIs for managing driver operational profiles")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @Operation(summary = "Create a new driver profile")
    @ApiResponse(responseCode = "201", description = "Driver created successfully")
    public ResponseEntity<DriverResponse> createDriver(@Valid @RequestBody DriverCreateRequest request) {
        DriverResponse response = driverService.createDriver(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{driverId}")
    @Operation(summary = "Get driver profile by ID")
    @ApiResponse(responseCode = "200", description = "Driver found")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<DriverResponse> getDriver(@PathVariable String driverId) {
        DriverResponse response = driverService.getDriver(driverId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{driverId}")
    @Operation(summary = "Update driver profile")
    @ApiResponse(responseCode = "200", description = "Driver updated successfully")
    public ResponseEntity<DriverResponse> updateDriver(@PathVariable String driverId, @Valid @RequestBody DriverUpdateRequest request) {
        DriverResponse response = driverService.updateDriver(driverId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{driverId}")
    @Operation(summary = "Delete driver profile")
    @ApiResponse(responseCode = "204", description = "Driver deleted")
    public ResponseEntity<Void> deleteDriver(@PathVariable String driverId) {
        driverService.deleteDriver(driverId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{driverId}/availability")
    @Operation(summary = "Get driver availability status")
    @ApiResponse(responseCode = "200", description = "Driver availability retrieved")
    public ResponseEntity<DriverResponse> getDriverAvailability(@PathVariable String driverId) {
        DriverResponse response = driverService.getDriver(driverId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{driverId}/availability")
    @Operation(summary = "Update driver availability status")
    @ApiResponse(responseCode = "200", description = "Driver availability updated")
    public ResponseEntity<DriverResponse> updateDriverAvailability(@PathVariable String driverId, @Valid @RequestBody AvailabilityUpdateRequest request) {
        DriverResponse response = driverService.updateAvailability(driverId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{driverId}/location")
    @Operation(summary = "Get driver simulated location")
    @ApiResponse(responseCode = "200", description = "Driver location retrieved")
    public ResponseEntity<LocationResponse> getDriverLocation(@PathVariable String driverId) {
        LocationResponse response = driverService.getLocation(driverId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{driverId}/location")
    @Operation(summary = "Update driver simulated location")
    @ApiResponse(responseCode = "200", description = "Driver location updated")
    public ResponseEntity<LocationResponse> updateDriverLocation(@PathVariable String driverId, @Valid @RequestBody LocationUpdateRequest request) {
        LocationResponse response = driverService.updateLocation(driverId, request);
        return ResponseEntity.ok(response);
    }
}
