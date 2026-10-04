package lk.sliit.ridelink.drivvehicle.service;

import lk.sliit.ridelink.drivvehicle.dto.EligibleDriverListResponse;
import lk.sliit.ridelink.drivvehicle.entity.Driver;
import lk.sliit.ridelink.drivvehicle.entity.Vehicle;
import lk.sliit.ridelink.drivvehicle.enums.AvailabilityStatus;
import lk.sliit.ridelink.drivvehicle.enums.DriverStatus;
import lk.sliit.ridelink.drivvehicle.enums.VehicleStatus;
import lk.sliit.ridelink.drivvehicle.enums.VehicleType;
import lk.sliit.ridelink.drivvehicle.repository.DriverRepository;
import lk.sliit.ridelink.drivvehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverEligibilityServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DriverEligibilityService driverEligibilityService;

    private Driver eligibleDriver;
    private Vehicle activeVehicle;

    @BeforeEach
    void setUp() {
        eligibleDriver = Driver.builder()
                .driverId("D-1001")
                .fullName("Sunil Shantha")
                .driverStatus(DriverStatus.ACTIVE)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .serviceArea("Kottawa")
                .latitude(new BigDecimal("6.8407"))
                .longitude(new BigDecimal("79.9542"))
                .build();

        activeVehicle = Vehicle.builder()
                .vehicleId("V-1001")
                .driverId("D-1001")
                .make("Suzuki")
                .model("Alto")
                .vehicleType(VehicleType.HATCHBACK)
                .capacity(3)
                .status(VehicleStatus.ACTIVE)
                .build();
    }

    @Test
    void testEligibleDriver_Returned() {
        when(driverRepository.findByDriverStatusAndAvailabilityStatusAndServiceArea(
                DriverStatus.ACTIVE, AvailabilityStatus.AVAILABLE, "Kottawa"))
                .thenReturn(Collections.singletonList(eligibleDriver));
        
        when(vehicleRepository.findByDriverIdAndStatus("D-1001", VehicleStatus.ACTIVE))
                .thenReturn(Optional.of(activeVehicle));

        EligibleDriverListResponse response = driverEligibilityService.getEligibleDrivers("Kottawa");
        List<lk.sliit.ridelink.drivvehicle.dto.EligibleDriverResponse> result = response.getDrivers();

        assertEquals(1, result.size());
        assertEquals("D-1001", result.get(0).getDriverId());
        assertEquals(VehicleType.HATCHBACK, result.get(0).getVehicleType());
    }

    @Test
    void testUnavailableDriver_Excluded() {
        // Query returns no UNAVAILABLE drivers since they're filtered in DB layer
        when(driverRepository.findByDriverStatusAndAvailabilityStatusAndServiceArea(
                DriverStatus.ACTIVE, AvailabilityStatus.AVAILABLE, "Kottawa"))
                .thenReturn(Collections.emptyList());

        EligibleDriverListResponse response = driverEligibilityService.getEligibleDrivers("Kottawa");
        List<lk.sliit.ridelink.drivvehicle.dto.EligibleDriverResponse> result = response.getDrivers();

        assertTrue(result.isEmpty());
    }

    @Test
    void testNoActiveVehicle_Excluded() {
        when(driverRepository.findByDriverStatusAndAvailabilityStatusAndServiceArea(
                DriverStatus.ACTIVE, AvailabilityStatus.AVAILABLE, "Kottawa"))
                .thenReturn(Collections.singletonList(eligibleDriver));
                
        when(vehicleRepository.findByDriverIdAndStatus("D-1001", VehicleStatus.ACTIVE))
                .thenReturn(Optional.empty());

        EligibleDriverListResponse response = driverEligibilityService.getEligibleDrivers("Kottawa");
        List<lk.sliit.ridelink.drivvehicle.dto.EligibleDriverResponse> result = response.getDrivers();

        assertTrue(result.isEmpty());
    }

    @Test
    void testNoLocation_Excluded() {
        Driver driverNoLocation = Driver.builder()
                .driverId("D-1002")
                .driverStatus(DriverStatus.ACTIVE)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .serviceArea("Kottawa")
                .build(); // No lat/lng
                
        when(driverRepository.findByDriverStatusAndAvailabilityStatusAndServiceArea(
                DriverStatus.ACTIVE, AvailabilityStatus.AVAILABLE, "Kottawa"))
                .thenReturn(Collections.singletonList(driverNoLocation));
                
        EligibleDriverListResponse response = driverEligibilityService.getEligibleDrivers("Kottawa");
        List<lk.sliit.ridelink.drivvehicle.dto.EligibleDriverResponse> result = response.getDrivers();

        assertTrue(result.isEmpty());
    }

    @Test
    void testEmptyResult_NoDrivers() {
        when(driverRepository.findByDriverStatusAndAvailabilityStatusAndServiceArea(
                DriverStatus.ACTIVE, AvailabilityStatus.AVAILABLE, "Kandy"))
                .thenReturn(Collections.emptyList());

        EligibleDriverListResponse response = driverEligibilityService.getEligibleDrivers("Kandy");
        List<lk.sliit.ridelink.drivvehicle.dto.EligibleDriverResponse> result = response.getDrivers();

        assertTrue(result.isEmpty());
    }

    @Test
    void testMultipleEligibleDrivers() {
        Driver driver2 = Driver.builder()
                .driverId("D-1002")
                .fullName("Nimal Perera")
                .driverStatus(DriverStatus.ACTIVE)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .serviceArea("Kottawa")
                .latitude(new BigDecimal("6.8407"))
                .longitude(new BigDecimal("79.9542"))
                .build();
                
        when(driverRepository.findByDriverStatusAndAvailabilityStatusAndServiceArea(
                DriverStatus.ACTIVE, AvailabilityStatus.AVAILABLE, "Kottawa"))
                .thenReturn(Arrays.asList(eligibleDriver, driver2));
                
        when(vehicleRepository.findByDriverIdAndStatus("D-1001", VehicleStatus.ACTIVE))
                .thenReturn(Optional.of(activeVehicle));
        when(vehicleRepository.findByDriverIdAndStatus("D-1002", VehicleStatus.ACTIVE))
                .thenReturn(Optional.of(activeVehicle));

        EligibleDriverListResponse response = driverEligibilityService.getEligibleDrivers("Kottawa");
        List<lk.sliit.ridelink.drivvehicle.dto.EligibleDriverResponse> result = response.getDrivers();

        assertEquals(2, result.size());
    }
}
