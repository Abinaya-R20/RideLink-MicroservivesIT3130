# RideLink – Ride Management Microservice

Member 3 service for the RideLink backend-only group assignment. This implementation uses Java 17, Spring Boot, Maven, Spring Data JPA and H2 for a local viva demonstration. It is **not MERN**.

## 1. Requirements
- JDK 17+
- Maven 3.8+
- Visual Studio Code with Extension Pack for Java and Spring Boot Extension Pack
- Driver Service running on `http://localhost:8082`
- An access token issued by the group's Account Service

## 2. Important integration contract
The supplied assignment screenshot specifies:
- Ride Service port `8083`
- Driver Service port `8082`
- `GET /internal/drivers/available`
- `PATCH /internal/drivers/{id}/status`
- Header `X-Internal-Key`
- JWT signed with HS256 and shared `JWT_SECRET`; JWT `sub` is the account id and `role` is `PASSENGER`, `DRIVER` or `ADMIN`
- Error JSON fields: `timestamp`, `status`, `error`, `message`, `path`

This Ride Service expects Driver Service's available endpoint to return a JSON array such as:
```json
[
  {"driverId":"driver-101"},
  {"driverId":"driver-102"}
]
```
It sends `{"status":"BUSY"}` when reserving a driver and `{"status":"AVAILABLE"}` when releasing one after completion/cancellation. Confirm these payloads and status enum values with Member 2. If their API response uses a different field/shape, update `DriverCandidate.java` / `DriverClient.java` to match the agreed contract. This matters for integration.

When Driver Service returns HTTP `409 Conflict` while setting a candidate to `BUSY`, Ride Service treats that candidate as already taken and tries the next candidate.

## 3. Open in VS Code and run
1. Extract the ZIP folder.
2. Open VS Code → **File → Open Folder** → choose `ridelink-ride-service`.
3. Confirm JDK 17 is selected. Open the terminal (**Terminal → New Terminal**).
4. Configure the shared JWT secret and internal service key through environment
   variables. Use the same values as the corresponding services, and do not
   commit the values to the repository.

Windows PowerShell:
```powershell
$env:JWT_SECRET="<your-shared-jwt-secret>"
$env:INTERNAL_SERVICE_KEY="<your-internal-service-key>"
$env:DRIVER_SERVICE_URL="http://localhost:8082"
```

macOS/Linux:
```bash
export JWT_SECRET="<your-shared-jwt-secret>"
export INTERNAL_SERVICE_KEY="<your-internal-service-key>"
export DRIVER_SERVICE_URL="http://localhost:8082"
```

5. Start Driver Service first (and Account Service if you need to obtain a real JWT).
6. In the Ride Service terminal run:
```bash
mvn clean spring-boot:run
```
7. Confirm startup in terminal: `Started RideServiceApplication`. The service listens on `http://localhost:8083`.
8. Open Swagger UI: `http://localhost:8083/swagger-ui.html`.

To run tests:
```bash
mvn test
```

## 4. Endpoints
All `/rides` endpoints require `Authorization: Bearer <JWT>`.

| Method | Path | Purpose |
|---|---|---|
| POST | `/rides` | Passenger requests a ride; Ride Service obtains eligible drivers and reserves the first available candidate |
| GET | `/rides/my` | List rides belonging to current passenger/driver; Admin sees all |
| GET | `/rides/{id}` | Retrieve a ride, subject to ownership |
| PATCH | `/rides/{id}/status` | Update ride lifecycle status |

### Create a ride
```http
POST http://localhost:8083/rides
Authorization: Bearer <PASSENGER_JWT>
Content-Type: application/json
```
```json
{"pickup":"SLIIT Malabe","destination":"Colombo Fort"}
```
Successful response is `201 Created` with a ride containing an ID, passenger ID, driver ID and status `ASSIGNED`.

### Ride lifecycle
Assigned driver sends these in order:
```json
{"status":"ACCEPTED"}
{"status":"IN_PROGRESS"}
{"status":"COMPLETED"}
```
Passenger or assigned driver may cancel an `ASSIGNED` or `ACCEPTED` ride:
```json
{"status":"CANCELLED"}
```
Invalid transitions return `409 Conflict`. Users cannot retrieve or update rides they do not own/drive. Admin may view and progress rides.

### Postman demo sequence
1. Get a passenger JWT from Account Service and set Postman variable `token`.
2. `POST /rides` with pickup/destination; save returned `id` as `rideId`.
3. `GET /rides/{{rideId}}` using passenger token.
4. Sign in as the assigned driver through Account Service; use that driver's JWT.
5. `PATCH /rides/{{rideId}}/status` with `ACCEPTED`.
6. Repeat with `IN_PROGRESS`, then `COMPLETED`.
7. `GET /rides/my` with passenger token to show completed ride.
8. Negative test: attempt `IN_PROGRESS` directly from `ASSIGNED`; expect `409 INVALID_STATUS_TRANSITION`.
9. Negative test: another passenger tries to fetch this ride; expect `403 FORBIDDEN`.
10. Negative test: no available drivers; expect `409 NO_AVAILABLE_DRIVER`.

The Account Service must issue a JWT whose `sub` contains the account ID and `role` contains the agreed role. Use the same JWT secret across all services. Do not paste a real secret or token into Git.

## 5. Error response
Errors use the shared shape:
```json
{
  "timestamp":"2026-09-28T09:00:00Z",
  "status":409,
  "error":"INVALID_STATUS_TRANSITION",
  "message":"Allowed progress: ASSIGNED -> ACCEPTED -> IN_PROGRESS -> COMPLETED",
  "path":"/rides/ride-id/status"
}
```

## 6. Design explanation for viva
- Ride Service owns the `rides` table. It never reads or writes the Driver Service database.
- It uses synchronous REST because driver eligibility and reservation are needed before responding to a ride request.
- Candidate reservation is attempted one by one. A `409` from Driver Service means the candidate was taken concurrently, so the next candidate is tried.
- Ride lifecycle rules are enforced in the service layer, not trusted to the API caller.
- JWT authentication identifies the caller; role and ownership checks control access.
- H2 is in-memory for quick local demonstration. It resets when the app restarts; use the database agreed by the group if persistent storage is required.
- Fare calculation and payment recording belong to Member 4's Fare & Payment Service, not this service.

## 7. Academic integrity and contribution
Use this as a learning scaffold: read each class, run it, adapt it to your group's agreed API contracts, add tests, and make commits that reflect your own work. The assignment brief says students must understand and explain their code, maintain traceable individual contributions, and follow the institute's generative-AI disclosure policy. Do not claim work you did not do or fabricate Git history.
