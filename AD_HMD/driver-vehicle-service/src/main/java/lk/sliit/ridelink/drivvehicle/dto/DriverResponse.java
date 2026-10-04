package lk.sliit.ridelink.drivvehicle.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class DriverResponse {
    private String driverId;
    private String accountId;
    private String licenseNumber;
    private String fullName;
    private String phone;
    private DriverStatus driverStatus;
    private AvailabilityStatus availabilityStatus;
    private String serviceArea;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime locationUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
