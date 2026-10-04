package lk.sliit.ridelink.drivvehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.sliit.ridelink.drivvehicle.enums.AvailabilityStatus;
import lk.sliit.ridelink.drivvehicle.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverCreateRequest {
    @NotBlank
    private String accountId;
    
    @NotBlank
    private String licenseNumber;
    
    @NotBlank
    private String fullName;
    
    private String phone;
    
    @NotNull
    private DriverStatus driverStatus;
    
    @NotNull
    private AvailabilityStatus availabilityStatus;
    
    private String serviceArea;
}
