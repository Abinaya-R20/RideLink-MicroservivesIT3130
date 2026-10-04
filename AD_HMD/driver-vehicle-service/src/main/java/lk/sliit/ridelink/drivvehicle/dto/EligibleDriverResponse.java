package lk.sliit.ridelink.drivvehicle.dto;

import java.math.BigDecimal;
import lk.sliit.ridelink.drivvehicle.enums.AvailabilityStatus;
import lk.sliit.ridelink.drivvehicle.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleDriverResponse {
    private String driverId;
    private String vehicleId;
    private VehicleType vehicleType;
    private String serviceArea;
    private AvailabilityStatus availabilityStatus;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
