package lk.sliit.ridelink.drivvehicle.service;

import lk.sliit.ridelink.drivvehicle.dto.VehicleRequest;
import lk.sliit.ridelink.drivvehicle.dto.VehicleResponse;
import lk.sliit.ridelink.drivvehicle.entity.Vehicle;
import lk.sliit.ridelink.drivvehicle.exception.DriverNotFoundException;
import lk.sliit.ridelink.drivvehicle.exception.DuplicateResourceException;
import lk.sliit.ridelink.drivvehicle.exception.VehicleNotFoundException;
import lk.sliit.ridelink.drivvehicle.repository.DriverRepository;
import lk.sliit.ridelink.drivvehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    @Transactional
    public VehicleResponse createVehicle(String driverId, VehicleRequest request) {
        if (!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException(driverId);
        }

        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException("Vehicle with registration number already exists: " + request.getRegistrationNumber());
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setDriverId(driverId);
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setColor(request.getColor());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setStatus(request.getStatus());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToResponse(savedVehicle);
    }

    @Transactional(readOnly = true)
    public VehicleResponse getVehicleByDriverId(String driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException(driverId);
        }

        List<Vehicle> vehicles = vehicleRepository.findByDriverId(driverId);
        if (vehicles.isEmpty()) {
            throw new VehicleNotFoundException("No vehicle found for driver: " + driverId);
        }
        
        return mapToResponse(vehicles.get(0));
    }

    @Transactional
    public VehicleResponse updateVehicle(String driverId, VehicleRequest request) {
        if (!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException(driverId);
        }

        List<Vehicle> vehicles = vehicleRepository.findByDriverId(driverId);
        if (vehicles.isEmpty()) {
            throw new VehicleNotFoundException("No vehicle found for driver: " + driverId);
        }
        
        Vehicle vehicle = vehicles.get(0);
        
        if (!request.getRegistrationNumber().equals(vehicle.getRegistrationNumber()) &&
            vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException("Vehicle with registration number already exists: " + request.getRegistrationNumber());
        }

        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setColor(request.getColor());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setStatus(request.getStatus());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return mapToResponse(updatedVehicle);
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .vehicleId(vehicle.getVehicleId())
                .driverId(vehicle.getDriverId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .color(vehicle.getColor())
                .vehicleType(vehicle.getVehicleType())
                .capacity(vehicle.getCapacity())
                .status(vehicle.getStatus())
                .build();
    }
}
