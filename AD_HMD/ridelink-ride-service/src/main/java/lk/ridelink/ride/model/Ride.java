package lk.ridelink.ride.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rides")
public class Ride {
    @Id
    private String id;
    @Column(nullable = false)
    private String passengerId;
    private String driverId;
    @Column(nullable = false)
    private String pickup;
    @Column(nullable = false)
    private String destination;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    protected Ride() {}

    public Ride(String passengerId, String driverId, String pickup, String destination) {
        this.id = UUID.randomUUID().toString();
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.pickup = pickup;
        this.destination = destination;
        this.status = RideStatus.ASSIGNED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void changeStatus(RideStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getPassengerId() { return passengerId; }
    public String getDriverId() { return driverId; }
    public String getPickup() { return pickup; }
    public String getDestination() { return destination; }
    public RideStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
