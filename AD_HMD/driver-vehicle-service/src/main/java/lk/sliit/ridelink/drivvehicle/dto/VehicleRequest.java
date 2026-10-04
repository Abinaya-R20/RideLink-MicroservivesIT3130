package lk.sliit.ridelink.drivvehicle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.sliit.ridelink.drivvehicle.enums.VehicleStatus;
import lk.sliit.ridelink.drivvehicle.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {
    @NotBlank
    private String registrationNumber;
    
    @NotBlank
    private String make;
    
    @NotBlank
    private String model;
    
    @NotNull
    @Min(1900)
    @Max(2030)
    private Integer year;
    
    private String color;
    
    @NotNull
    private VehicleType vehicleType;
    
    @NotNull
    @Min(1)
    @Max(50)
    private Integer capacity;
    
    @NotNull
    private VehicleStatus status;
}
