package lk.sliit.ridelink.drivvehicle.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private String driverId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime locationUpdatedAt;
}
