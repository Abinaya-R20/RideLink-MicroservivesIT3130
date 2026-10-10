package lk.ridelink.ride.repository;

import lk.ridelink.ride.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, String> {
    List<Ride> findByPassengerIdOrderByCreatedAtDesc(String passengerId);
    List<Ride> findByDriverIdOrderByCreatedAtDesc(String driverId);
}
