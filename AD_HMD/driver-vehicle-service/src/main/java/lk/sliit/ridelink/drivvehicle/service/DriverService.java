package lk.sliit.ridelink.drivvehicle.service;

import lk.sliit.ridelink.drivvehicle.dto.*;
import lk.sliit.ridelink.drivvehicle.entity.Driver;
import lk.sliit.ridelink.drivvehicle.enums.AvailabilityStatus;
import lk.sliit.ridelink.drivvehicle.enums.DriverStatus;
import lk.sliit.ridelink.drivvehicle.exception.DriverNotFoundException;
import lk.sliit.ridelink.drivvehicle.exception.DuplicateResourceException;
import lk.sliit.ridelink.drivvehicle.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    @Transactional
    public DriverResponse createDriver(DriverCreateRequest request) {
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("Driver with license number already exists: " + request.getLicenseNumber());
        }

        Driver driver = new Driver();
        driver.setAccountId(request.getAccountId());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setFullName(request.getFullName());
        driver.setPhone(request.getPhone());
        driver.setDriverStatus(DriverStatus.ACTIVE); // Default
        driver.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE); // Default
        driver.setServiceArea(request.getServiceArea());

        Driver savedDriver = driverRepository.save(driver);
        return mapToResponse(savedDriver);
    }

    @Transactional(readOnly = true)
    public DriverResponse getDriver(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        return mapToResponse(driver);
    }

    @Transactional
    public DriverResponse updateDriver(String driverId, DriverUpdateRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));

        if (request.getLicenseNumber() != null) {
            if (!request.getLicenseNumber().equals(driver.getLicenseNumber()) &&
                    driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                throw new DuplicateResourceException("Driver with license number already exists: " + request.getLicenseNumber());
            }
            driver.setLicenseNumber(request.getLicenseNumber());
        }

        if (request.getFullName() != null) {
            driver.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            driver.setPhone(request.getPhone());
        }
        if (request.getDriverStatus() != null) {
            driver.setDriverStatus(request.getDriverStatus());
        }
        if (request.getServiceArea() != null) {
            driver.setServiceArea(request.getServiceArea());
        }

        Driver updatedDriver = driverRepository.save(driver);
        return mapToResponse(updatedDriver);
    }

    @Transactional
    public void deleteDriver(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        driverRepository.delete(driver);
    }

    @Transactional
    public DriverResponse updateAvailability(String driverId, AvailabilityUpdateRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        
        driver.setAvailabilityStatus(request.getAvailabilityStatus());
        Driver updatedDriver = driverRepository.save(driver);
        return mapToResponse(updatedDriver);
    }

    @Transactional(readOnly = true)
    public DriverResponse getAvailability(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        return mapToResponse(driver);
    }

    @Transactional
    public LocationResponse updateLocation(String driverId, LocationUpdateRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        
        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());
        driver.setLocationUpdatedAt(LocalDateTime.now());
        
        Driver updatedDriver = driverRepository.save(driver);
        return mapToLocationResponse(updatedDriver);
    }

    @Transactional(readOnly = true)
    public LocationResponse getLocation(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        return mapToLocationResponse(driver);
    }

    private DriverResponse mapToResponse(Driver driver) {
        return DriverResponse.builder()
                .driverId(driver.getDriverId())
                .accountId(driver.getAccountId())
                .licenseNumber(driver.getLicenseNumber())
                .fullName(driver.getFullName())
                .phone(driver.getPhone())
                .driverStatus(driver.getDriverStatus())
                .availabilityStatus(driver.getAvailabilityStatus())
                .serviceArea(driver.getServiceArea())
                .latitude(driver.getLatitude())
                .longitude(driver.getLongitude())
                .locationUpdatedAt(driver.getLocationUpdatedAt())
                .build();
    }

    private LocationResponse mapToLocationResponse(Driver driver) {
        return LocationResponse.builder()
                .driverId(driver.getDriverId())
                .latitude(driver.getLatitude())
                .longitude(driver.getLongitude())
                .locationUpdatedAt(driver.getLocationUpdatedAt())
                .build();
    }
}
