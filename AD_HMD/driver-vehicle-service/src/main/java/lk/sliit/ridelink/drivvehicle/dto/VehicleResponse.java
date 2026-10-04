package lk.sliit.ridelink.drivvehicle.dto;

import java.time.LocalDateTime;
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
public class VehicleResponse {
    private String vehicleId;
    private String driverId;
    private String registrationNumber;
    private String make;
    private String model;
    private int year;
    private String color;
    private VehicleType vehicleType;
    private int capacity;
    private VehicleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
