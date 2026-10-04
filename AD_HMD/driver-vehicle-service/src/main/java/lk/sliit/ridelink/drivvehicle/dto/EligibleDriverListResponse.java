package lk.sliit.ridelink.drivvehicle.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleDriverListResponse {
    private List<EligibleDriverResponse> drivers;
}
