package lk.sliit.ridelink.drivvehicle.dto;

import jakarta.validation.constraints.NotNull;
import lk.sliit.ridelink.drivvehicle.enums.AvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityUpdateRequest {
    @NotNull
    private AvailabilityStatus availabilityStatus;
}
