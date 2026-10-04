package lk.sliit.ridelink.drivvehicle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.sliit.ridelink.drivvehicle.dto.AvailabilityUpdateRequest;
import lk.sliit.ridelink.drivvehicle.dto.DriverCreateRequest;
import lk.sliit.ridelink.drivvehicle.dto.DriverResponse;
import lk.sliit.ridelink.drivvehicle.dto.LocationResponse;
import lk.sliit.ridelink.drivvehicle.dto.LocationUpdateRequest;
import lk.sliit.ridelink.drivvehicle.enums.AvailabilityStatus;
import lk.sliit.ridelink.drivvehicle.enums.DriverStatus;
import lk.sliit.ridelink.drivvehicle.exception.DriverNotFoundException;
import lk.sliit.ridelink.drivvehicle.exception.GlobalExceptionHandler;
import lk.sliit.ridelink.drivvehicle.service.DriverEligibilityService;
import lk.sliit.ridelink.drivvehicle.service.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@SuppressWarnings("null")
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;
    
    @MockBean
    private DriverEligibilityService driverEligibilityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateDriver_Returns201() throws Exception {
        DriverCreateRequest request = new DriverCreateRequest("A-1001", "B-1234567", "Kamal Perera", "0712345678",
                DriverStatus.ACTIVE, AvailabilityStatus.UNAVAILABLE, "Colombo");
        DriverResponse response = new DriverResponse("D-1001", "A-1001", "B-1234567", "Kamal Perera", "0712345678",
                DriverStatus.ACTIVE, AvailabilityStatus.UNAVAILABLE, "Colombo", null, null, null, LocalDateTime.now(), LocalDateTime.now());

        when(driverService.createDriver(any(DriverCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.driverId").value("D-1001"))
                .andExpect(jsonPath("$.fullName").value("Kamal Perera"));
    }

    @Test
    void testGetDriver_Returns200() throws Exception {
        DriverResponse response = new DriverResponse("D-1001", "A-1001", "B-1234567", "Kamal Perera", "0712345678",
                DriverStatus.ACTIVE, AvailabilityStatus.UNAVAILABLE, "Colombo", null, null, null, LocalDateTime.now(), LocalDateTime.now());

        when(driverService.getDriver("D-1001")).thenReturn(response);

        mockMvc.perform(get("/api/drivers/D-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value("D-1001"))
                .andExpect(jsonPath("$.fullName").value("Kamal Perera"));
    }

    @Test
    void testGetDriver_Returns404() throws Exception {
        when(driverService.getDriver("D-9999")).thenThrow(new DriverNotFoundException("D-9999"));

        mockMvc.perform(get("/api/drivers/D-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Driver not found with ID: D-9999"));
    }

    @Test
    void testUpdateAvailability_Returns200() throws Exception {
        AvailabilityUpdateRequest request = new AvailabilityUpdateRequest(AvailabilityStatus.AVAILABLE);
        DriverResponse response = new DriverResponse("D-1001", "A-1001", "B-1234567", "Kamal Perera", "0712345678",
                DriverStatus.ACTIVE, AvailabilityStatus.AVAILABLE, "Colombo", null, null, null, LocalDateTime.now(), LocalDateTime.now());

        when(driverService.updateAvailability(eq("D-1001"), any(AvailabilityUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/drivers/D-1001/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availabilityStatus").value("AVAILABLE"));
    }

    @Test
    void testUpdateLocation_Returns200() throws Exception {
        LocationUpdateRequest request = new LocationUpdateRequest(new BigDecimal("6.8731"), new BigDecimal("79.8892"));
        LocationResponse response = new LocationResponse("D-1001", new BigDecimal("6.8731"), new BigDecimal("79.8892"), LocalDateTime.now());

        when(driverService.updateLocation(eq("D-1001"), any(LocationUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/drivers/D-1001/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(6.8731))
                .andExpect(jsonPath("$.longitude").value(79.8892));
    }
}
