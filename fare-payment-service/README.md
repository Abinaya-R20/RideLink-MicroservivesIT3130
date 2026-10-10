# RideLink – Fare & Payment Service

## Overview

The Fare & Payment Service is one of the four microservices developed for the RideLink system as part of the IT3130 – Application Development Group Assignment.

This service is responsible for:

- Fare estimation
- Final fare calculation
- Payment recording
- Payment status management
- Receipt generation and retrieval
- JWT-based authentication
- Error handling

**Developed by:** Mathushalini Poobalasingam  
**Student ID:** IT24102015

## Technology Stack

- Java 21
- Spring Boot
- Spring Data MongoDB
- Spring Security
- MongoDB Atlas
- Maven
- Swagger / OpenAPI
- Postman
- JUnit 5
- Mockito

## Service Configuration

**Port:** `8084`  
**Database:** `ridelink_fare_payment`

Required environment variables:

```text
MONGODB_URI
JWT_SECRET
INTERNAL_SERVICE_KEY
Sensitive values are not stored in the source code.

## Fare Calculation

```text
Base Fare = 200
Rate Per Kilometer = 100

Fare = Base Fare + (Distance × Rate Per Kilometer)
```

Example for 10 km:

```text
Fare = 200 + (10 × 100)
Fare = 1200
```

## API Endpoints

### Fare APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/fares` | Create a fare |
| GET | `/api/fares` | Retrieve all fares |
| GET | `/api/fares/estimate?distanceKm={distance}` | Calculate estimated fare |
| GET | `/api/fares/final?distanceKm={distance}` | Calculate final fare |
| PUT | `/api/fares/finalize/{rideId}?distanceKm={distance}` | Finalize fare for a ride |

### Payment APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payments` | Record a payment |
| GET | `/api/payments/ride/{rideId}` | Retrieve payment by ride ID |

### Receipt APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/receipts/ride/{rideId}` | Generate a receipt |
| GET | `/api/receipts/ride/{rideId}` | Retrieve receipt by ride ID |

## Authentication

API endpoints under `/api/**` require JWT authentication.

Swagger/OpenAPI endpoints are publicly accessible.

Example:

```text
Authorization: Bearer <JWT_TOKEN>
```

## Running the Service

Make sure the required environment variables are configured.

On Windows:

```bash
.\mvnw.cmd spring-boot:run
```

The service runs on:

```text
http://localhost:8084
```

## Swagger UI

```text
http://localhost:8084/swagger-ui/index.html
```

## Testing

Run the tests using:

```bash
.\mvnw.cmd clean test
```

Test coverage:

- Fare Service – 4 tests
- Payment Service – 6 tests
- Receipt Service – 5 tests
- Spring application context – 1 test

**Total: 16 tests**

## Workflow

```text
Fare Estimation
      ↓
Fare Finalization
      ↓
Payment
      ↓
Receipt Generation
```

A payment can only be recorded after the fare has been finalized. The payment amount is obtained from the finalized fare.

## Assignment Information

**Module:** IT3130 – Application Development  
**System:** RideLink  
**Microservice:** Fare & Payment Service  
**Student ID:** IT24102015
