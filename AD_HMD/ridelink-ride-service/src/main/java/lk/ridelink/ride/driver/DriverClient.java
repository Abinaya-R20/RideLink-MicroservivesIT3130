package lk.ridelink.ride.driver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Component
public class DriverClient {
    private final RestClient client;
    private final String internalKey;

    public DriverClient(@Value("${ridelink.driver.base-url}") String baseUrl,
                        @Value("${ridelink.internal-key}") String internalKey) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
        this.internalKey = internalKey;
    }

    public List<DriverCandidate> availableDrivers() {
        List<DriverCandidate> result = client.get()
            .uri("/internal/drivers/available")
            .header("X-Internal-Key", internalKey)
            .retrieve()
            .body(new ParameterizedTypeReference<List<DriverCandidate>>() {});
        return result == null ? List.of() : result;
    }

    public void setDriverStatus(String driverId, String status) {
        client.patch()
            .uri("/internal/drivers/{id}/status", driverId)
            .header("X-Internal-Key", internalKey)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .body(Map.of("status", status))
            .retrieve()
            .toBodilessEntity();
    }
}
