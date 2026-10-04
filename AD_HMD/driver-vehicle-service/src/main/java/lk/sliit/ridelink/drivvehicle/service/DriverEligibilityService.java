package lk.sliit.ridelink.drivvehicle.service;

import lk.sliit.ridelink.drivvehicle.dto.EligibleDriverListResponse;
import lk.sliit.ridelink.drivvehicle.dto.EligibleDriverResponse;
import lk.sliit.ridelink.drivvehicle.entity.Driver;
import lk.sliit.ridelink.drivvehicle.entity.Vehicle;
import lk.sliit.ridelink.drivvehicle.enums.AvailabilityStatus;
import lk.sliit.ridelink.drivvehicle.enums.DriverStatus;
import lk.sliit.ridelink.drivvehicle.enums.VehicleStatus;
import lk.sliit.ridelink.drivvehicle.repository.DriverRepository;
import lk.sliit.ridelink.drivvehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverEligibilityService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public EligibleDriverListResponse getEligibleDrivers(String pickupArea) {
        List<Driver> activeAndAvailableDrivers = driverRepository.findByDriverStatusAndAvailabilityStatusAndServiceArea(
                DriverStatus.ACTIVE,
                AvailabilityStatus.AVAILABLE,
                pickupArea
        );

        List<EligibleDriverResponse> eligibleDrivers = new ArrayList<>();

        for (Driver driver : activeAndAvailableDrivers) {
            if (driver.getLatitude() != null && driver.getLongitude() != null) {
                Optional<Vehicle> activeVehicleOpt = vehicleRepository.findByDriverIdAndStatus(driver.getDriverId(), VehicleStatus.ACTIVE);
                
                if (activeVehicleOpt.isPresent()) {
                    Vehicle activeVehicle = activeVehicleOpt.get();
                    
                    EligibleDriverResponse response = EligibleDriverResponse.builder()
                            .driverId(driver.getDriverId())
                            .vehicleId(activeVehicle.getVehicleId())
                            .vehicleType(activeVehicle.getVehicleType())
                            .serviceArea(driver.getServiceArea())
                            .availabilityStatus(driver.getAvailabilityStatus())
                            .latitude(driver.getLatitude())
                            .longitude(driver.getLongitude())
                            .build();
                            
                    eligibleDrivers.add(response);
                }
            }
        }

        return new EligibleDriverListResponse(eligibleDrivers);
    }
}
