# RideLink Driver & Vehicle Service

Member 2 microservice for the RideLink ride-sharing platform.

## Tech Stack
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- MySQL
- Spring Security
- Springdoc OpenAPI (Swagger UI)
- JUnit 5 + Mockito
- Maven

## Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Database Setup
```sql
CREATE DATABASE ridelink_driver_vehicle;
```

Update `src/main/resources/application.properties` with your MySQL credentials.

### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

## API Documentation

Swagger UI: http://localhost:8082/swagger-ui.html
API Docs: http://localhost:8082/api-docs

## API Endpoints

### Driver Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/drivers | Create driver profile |
| GET | /api/drivers/{driverId} | Get driver profile |
| PUT | /api/drivers/{driverId} | Update driver profile |
| DELETE | /api/drivers/{driverId} | Delete driver |
| GET | /api/drivers/{driverId}/availability | Get availability |
| PATCH | /api/drivers/{driverId}/availability | Update availability |
| GET | /api/drivers/{driverId}/location | Get location |
| PATCH | /api/drivers/{driverId}/location | Update location |

### Vehicle Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/drivers/{driverId}/vehicle | Register vehicle |
| GET | /api/drivers/{driverId}/vehicle | Get vehicle |
| PUT | /api/drivers/{driverId}/vehicle | Update vehicle |

### Eligibility
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/drivers/eligible?pickupArea={area} | Get eligible drivers |

## Project Structure
- `config`: Security and open api configurations
- `controller`: REST controllers for drivers and vehicles
- `dto`: Request and response classes for API
- `entity`: Database models
- `enums`: Constants like VehicleType, AvailabilityStatus
- `exception`: Custom exceptions and global handlers
- `repository`: JPA interface repositories
- `service`: Business logic for the application
