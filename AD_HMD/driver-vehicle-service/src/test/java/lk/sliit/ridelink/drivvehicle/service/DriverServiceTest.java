package lk.sliit.ridelink.drivvehicle.service;

import lk.sliit.ridelink.drivvehicle.dto.*;
import lk.sliit.ridelink.drivvehicle.entity.Driver;
import lk.sliit.ridelink.drivvehicle.enums.AvailabilityStatus;
import lk.sliit.ridelink.drivvehicle.enums.DriverStatus;
import lk.sliit.ridelink.drivvehicle.exception.DriverNotFoundException;
import lk.sliit.ridelink.drivvehicle.exception.DuplicateResourceException;
import lk.sliit.ridelink.drivvehicle.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    private Driver driver;
    private DriverCreateRequest driverRequest;

    @BeforeEach
    void setUp() {
        driver = Driver.builder()
                .driverId("D-1001")
                .accountId("A-1001")
                .licenseNumber("B-1234567")
                .fullName("Kamal Perera")
                .phone("0712345678")
                .driverStatus(DriverStatus.ACTIVE)
                .availabilityStatus(AvailabilityStatus.UNAVAILABLE)
                .serviceArea("Colombo")
                .latitude(new BigDecimal("6.9271"))
                .longitude(new BigDecimal("79.8612"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        driverRequest = new DriverCreateRequest(
                "A-1001",
                "B-1234567",
                "Kamal Perera",
                "0712345678",
            DriverStatus.ACTIVE,
            AvailabilityStatus.UNAVAILABLE,
                "Colombo"
        );
    }

    @Test
    void testCreateDriver_Success() {
        when(driverRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        DriverResponse response = driverService.createDriver(driverRequest);

        assertNotNull(response);
        assertEquals("Kamal Perera", response.getFullName());
        assertEquals("B-1234567", response.getLicenseNumber());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void testCreateDriver_DuplicateLicense() {
        when(driverRepository.existsByLicenseNumber(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> driverService.createDriver(driverRequest));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void testGetDriver_Success() {
        when(driverRepository.findById("D-1001")).thenReturn(Optional.of(driver));

        DriverResponse response = driverService.getDriver("D-1001");

        assertNotNull(response);
        assertEquals("Kamal Perera", response.getFullName());
        verify(driverRepository, times(1)).findById("D-1001");
    }

    @Test
    void testGetDriver_NotFound() {
        when(driverRepository.findById("D-1001")).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.getDriver("D-1001"));
    }

    @Test
    void testUpdateDriver_Success() {
        when(driverRepository.findById("D-1001")).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        DriverUpdateRequest updateRequest = new DriverUpdateRequest("B-1234567", "Kamal Updated", "0777123456", DriverStatus.ACTIVE, "Kotte");
        DriverResponse response = driverService.updateDriver("D-1001", updateRequest);

        assertNotNull(response);
        assertEquals("Kamal Updated", driver.getFullName());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void testUpdateAvailability_Success() {
        when(driverRepository.findById("D-1001")).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        AvailabilityUpdateRequest request = new AvailabilityUpdateRequest(AvailabilityStatus.AVAILABLE);
        DriverResponse response = driverService.updateAvailability("D-1001", request);

        assertNotNull(response);
        assertEquals(AvailabilityStatus.AVAILABLE, response.getAvailabilityStatus());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void testUpdateLocation_Success() {
        when(driverRepository.findById("D-1001")).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        LocationUpdateRequest request = new LocationUpdateRequest(new BigDecimal("6.8731"), new BigDecimal("79.8892"));
        LocationResponse response = driverService.updateLocation("D-1001", request);

        assertNotNull(response);
        assertEquals(new BigDecimal("6.8731"), response.getLatitude());
        assertEquals(new BigDecimal("79.8892"), response.getLongitude());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void testDeleteDriver_Success() {
        when(driverRepository.findById("D-1001")).thenReturn(Optional.of(driver));
        driverService.deleteDriver("D-1001");

        verify(driverRepository, times(1)).delete(driver);
    }

    @Test
    void testDeleteDriver_NotFound() {
        when(driverRepository.findById("D-1001")).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.deleteDriver("D-1001"));
        verify(driverRepository, never()).delete(any(Driver.class));
    }
}
