package lk.sliit.ridelink.drivvehicle.dto;

import lk.sliit.ridelink.drivvehicle.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateRequest {
    private String licenseNumber;
    private String fullName;
    private String phone;
    private DriverStatus driverStatus;
    private String serviceArea;
}
