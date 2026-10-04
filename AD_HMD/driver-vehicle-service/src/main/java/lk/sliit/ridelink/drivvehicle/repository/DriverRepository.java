package lk.sliit.ridelink.drivvehicle.repository;

import lk.sliit.ridelink.drivvehicle.entity.Driver;
import lk.sliit.ridelink.drivvehicle.enums.AvailabilityStatus;
import lk.sliit.ridelink.drivvehicle.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    List<Driver> findByDriverStatusAndAvailabilityStatusAndServiceArea(DriverStatus driverStatus, AvailabilityStatus availabilityStatus, String serviceArea);
    Optional<Driver> findByAccountId(String accountId);
    boolean existsByLicenseNumber(String licenseNumber);
}
