package lk.sliit.ridelink.drivvehicle.service;

import lk.sliit.ridelink.drivvehicle.dto.VehicleRequest;
import lk.sliit.ridelink.drivvehicle.dto.VehicleResponse;
import lk.sliit.ridelink.drivvehicle.entity.Vehicle;
import lk.sliit.ridelink.drivvehicle.enums.VehicleStatus;
import lk.sliit.ridelink.drivvehicle.enums.VehicleType;
import lk.sliit.ridelink.drivvehicle.exception.DriverNotFoundException;
import lk.sliit.ridelink.drivvehicle.exception.DuplicateResourceException;
import lk.sliit.ridelink.drivvehicle.exception.VehicleNotFoundException;
import lk.sliit.ridelink.drivvehicle.repository.DriverRepository;
import lk.sliit.ridelink.drivvehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleRequest vehicleRequest;

    @BeforeEach
    void setUp() {
        vehicle = Vehicle.builder()
                .vehicleId("V-1001")
                .driverId("D-1001")
                .registrationNumber("WP CAR-1234")
                .make("Toyota")
                .model("Prius")
                .year(2018)
                .color("White")
                .vehicleType(VehicleType.SEDAN)
                .capacity(4)
                .status(VehicleStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        vehicleRequest = new VehicleRequest(
                "WP CAR-1234",
                "Toyota",
                "Prius",
                2018,
                "White",
                VehicleType.SEDAN,
                4,
                VehicleStatus.ACTIVE
        );
    }

    @Test
    void testCreateVehicle_Success() {
        when(driverRepository.existsById("D-1001")).thenReturn(true);
        when(vehicleRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse response = vehicleService.createVehicle("D-1001", vehicleRequest);

        assertNotNull(response);
        assertEquals("WP CAR-1234", response.getRegistrationNumber());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testCreateVehicle_DriverNotFound() {
        when(driverRepository.existsById("D-1001")).thenReturn(false);

        assertThrows(DriverNotFoundException.class, () -> vehicleService.createVehicle("D-1001", vehicleRequest));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void testCreateVehicle_DuplicateRegistration() {
        when(driverRepository.existsById("D-1001")).thenReturn(true);
        when(vehicleRepository.existsByRegistrationNumber(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> vehicleService.createVehicle("D-1001", vehicleRequest));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void testGetVehicle_Success() {
        when(driverRepository.existsById("D-1001")).thenReturn(true);
        when(vehicleRepository.findByDriverId("D-1001")).thenReturn(Collections.singletonList(vehicle));

        VehicleResponse response = vehicleService.getVehicleByDriverId("D-1001");

        assertNotNull(response);
        assertEquals("WP CAR-1234", response.getRegistrationNumber());
        verify(vehicleRepository, times(1)).findByDriverId("D-1001");
    }

    @Test
    void testGetVehicle_NotFound() {
        when(driverRepository.existsById("D-1001")).thenReturn(true);
        when(vehicleRepository.findByDriverId("D-1001")).thenReturn(Collections.emptyList());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.getVehicleByDriverId("D-1001"));
    }

    @Test
    void testUpdateVehicle_Success() {
        when(driverRepository.existsById("D-1001")).thenReturn(true);
        when(vehicleRepository.findByDriverId("D-1001")).thenReturn(Collections.singletonList(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleRequest updateRequest = new VehicleRequest("WP CAR-1234", "Honda", "Vezel", 2019, "Black", VehicleType.SUV, 4, VehicleStatus.ACTIVE);
        VehicleResponse response = vehicleService.updateVehicle("D-1001", updateRequest);

        assertNotNull(response);
        assertEquals("Honda", vehicle.getMake());
        verify(vehicleRepository, times(1)).save(vehicle);
    }
}
