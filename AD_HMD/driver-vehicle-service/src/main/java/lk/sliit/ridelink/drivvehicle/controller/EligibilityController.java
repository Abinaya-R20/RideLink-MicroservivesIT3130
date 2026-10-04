package lk.sliit.ridelink.drivvehicle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lk.sliit.ridelink.drivvehicle.dto.EligibleDriverListResponse;
import lk.sliit.ridelink.drivvehicle.service.DriverEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Eligibility", description = "API for retrieving eligible available drivers")
public class EligibilityController {

    private final DriverEligibilityService driverEligibilityService;

    @GetMapping("/eligible")
    @Operation(summary = "Retrieve eligible available drivers for a pickup area")
    @ApiResponse(responseCode = "200", description = "Eligible drivers retrieved successfully")
    public ResponseEntity<EligibleDriverListResponse> getEligibleDrivers(
            @Parameter(name = "pickupArea", description = "The pickup area to search for eligible drivers", required = true)
            @RequestParam String pickupArea) {
        
        EligibleDriverListResponse response = driverEligibilityService.getEligibleDrivers(pickupArea);
        return ResponseEntity.ok(response);
    }
}
