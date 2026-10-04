package lk.sliit.ridelink.drivvehicle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.sliit.ridelink.drivvehicle.dto.VehicleRequest;
import lk.sliit.ridelink.drivvehicle.dto.VehicleResponse;
import lk.sliit.ridelink.drivvehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers/{driverId}/vehicle")
@RequiredArgsConstructor
@Tag(name = "Vehicle Management", description = "APIs for managing vehicle details")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "Register a vehicle for a driver")
    @ApiResponse(responseCode = "201", description = "Vehicle registered successfully")
    public ResponseEntity<VehicleResponse> registerVehicle(@PathVariable String driverId, @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(driverId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get vehicle for a driver")
    @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable String driverId) {
        VehicleResponse response = vehicleService.getVehicleByDriverId(driverId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Update vehicle for a driver")
    @ApiResponse(responseCode = "200", description = "Vehicle updated successfully")
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable String driverId, @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(driverId, request);
        return ResponseEntity.ok(response);
    }
}
