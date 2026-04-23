## Happy Flow For Review

The flow below demonstrates a typical reviewer path:

1. verify seeded user
2. verify seeded event and show
3. inspect available seats
4. create a booking
5. confirm payment through webhook
6. verify the booking is completed

Set a base URL first:

```bash
BASE_URL=http://localhost:8080
```

### 1. Verify Seeded User

```bash
curl --location "$BASE_URL/api/v1/users/1"
```

### 2. Verify Seeded Event

```bash
curl --location "$BASE_URL/api/v1/events/1"
```

### 3. Verify Event Shows

```bash
curl --location "$BASE_URL/api/v1/events/1/shows"
```

### 4. Check Available Seats For Show 1

```bash
curl --location "$BASE_URL/api/v1/seat-inventories/by-show/1?status=AVAILABLE"
```

Expected result: available seat inventory entries including IDs such as `7`, `8`, and `9`.

### 5. Create A Booking

This uses the seeded user, seeded show, seeded category, and three available seat inventory IDs.

```bash
curl --location --request POST "$BASE_URL/api/v1/bookings" \
  --header "Content-Type: application/json" \
  --data '{
    "userId": 1,
    "showId": 1,
    "eventSeatCategoryId": 1,
    "seatInventoryIds": [7, 8, 9]
  }'
```

Expected result:

- HTTP `201`
- booking status `IN_PROGRESS`

Save the returned booking ID from the response for the next steps.

### 6. Fetch The Booking

Replace `<BOOKING_ID>` with the ID returned by the previous step.

```bash
curl --location "$BASE_URL/api/v1/bookings/<BOOKING_ID>?userId=1"
```

Expected result:

- booking status `IN_PROGRESS`
- total amount `1500`

### 7. Confirm Payment By Webhook

Replace `<BOOKING_ID>` with the same booking ID.

```bash
curl --location --request POST "$BASE_URL/api/v1/payments/webhook" \
  --header "Content-Type: application/json" \
  --header "X-Payment-Webhook-Secret: dev-webhook-secret" \
  --data '{
    "provider": "SANDBOX",
    "externalEventId": "evt-review-001",
    "bookingId": <BOOKING_ID>,
    "paymentStatus": "SUCCEEDED"
  }'
```

Expected result:

- HTTP `200`

### 8. Verify Booking Completion

```bash
curl --location "$BASE_URL/api/v1/bookings/<BOOKING_ID>?userId=1"
```

Expected result:

- booking status `COMPLETE`

### 9. Verify A Seat Is Marked Booked

```bash
curl --location "$BASE_URL/api/v1/seat-inventories/7"
```

Expected result:

- seat inventory status `BOOKED`

## Optional: Create New Data Instead Of Using Seeded Data

If a reviewer wants to validate create APIs directly, these are simple starter examples.

### Create User

```bash
curl --location --request POST "$BASE_URL/api/v1/users" \
  --header "Content-Type: application/json" \
  --data '{
    "name": "Integration User",
    "email": "integration.user@example.com",
    "mobileNumber": "+919900000001",
    "password": "CorrectHorse1"
  }'
```

### Create Event

```bash
curl --location --request POST "$BASE_URL/api/v1/events" \
  --header "Content-Type: application/json" \
  --data '{
    "name": "Test Event",
    "description": "Assignment demo event",
    "eventType": "CONCERT",
    "pricingType": "CATEGORY"
  }'
```

### Create Event Show

```bash
curl --location --request POST "$BASE_URL/api/v1/event-shows" \
  --header "Content-Type: application/json" \
  --data '{
    "eventId": 1,
    "venueId": 1,
    "startTime": "2027-01-01T18:00:00Z",
    "endTime": "2027-01-01T22:00:00Z"
  }'
```

## Suggested Review Approach

If you are evaluating this assignment, the fastest path is:

1. run the app with the `stage` profile
2. open Swagger UI
3. run `mvn test`
4. execute the happy flow in this README
5. inspect the concurrency integration test for overbooking protection

## Future Improvements

- add a Postman collection or `.http` request file for one-click API review
- add Docker Compose for PostgreSQL and app startup
- add CI workflow for build and test automation
## Happy Flow For Review

The flow below demonstrates a typical reviewer path:

1. verify seeded user
2. verify seeded event and show
3. inspect available seats
4. create a booking
5. confirm payment through webhook
6. verify the booking is completed

Set a base URL first:

```bash
BASE_URL=http://localhost:8080
```

### 1. Verify Seeded User

```bash
curl --location "$BASE_URL/api/v1/users/1"
```

### 2. Verify Seeded Event

```bash
curl --location "$BASE_URL/api/v1/events/1"
```

### 3. Verify Event Shows

```bash
curl --location "$BASE_URL/api/v1/events/1/shows"
```

### 4. Check Available Seats For Show 1

```bash
curl --location "$BASE_URL/api/v1/seat-inventories/by-show/1?status=AVAILABLE"
```

Expected result: available seat inventory entries including IDs such as `7`, `8`, and `9`.

### 5. Create A Booking

This uses the seeded user, seeded show, seeded category, and three available seat inventory IDs.

```bash
curl --location --request POST "$BASE_URL/api/v1/bookings" \
  --header "Content-Type: application/json" \
  --data '{
    "userId": 1,
    "showId": 1,
    "eventSeatCategoryId": 1,
    "seatInventoryIds": [7, 8, 9]
  }'
```

Expected result:

- HTTP `201`
- booking status `IN_PROGRESS`

Save the returned booking ID from the response for the next steps.

### 6. Fetch The Booking

Replace `<BOOKING_ID>` with the ID returned by the previous step.

```bash
curl --location "$BASE_URL/api/v1/bookings/<BOOKING_ID>?userId=1"
```

Expected result:

- booking status `IN_PROGRESS`
- total amount `1500`

### 7. Confirm Payment By Webhook

Replace `<BOOKING_ID>` with the same booking ID.

```bash
curl --location --request POST "$BASE_URL/api/v1/payments/webhook" \
  --header "Content-Type: application/json" \
  --header "X-Payment-Webhook-Secret: dev-webhook-secret" \
  --data '{
    "provider": "SANDBOX",
    "externalEventId": "evt-review-001",
    "bookingId": <BOOKING_ID>,
    "paymentStatus": "SUCCEEDED"
  }'
```

Expected result:

- HTTP `200`

### 8. Verify Booking Completion

```bash
curl --location "$BASE_URL/api/v1/bookings/<BOOKING_ID>?userId=1"
```

Expected result:

- booking status `COMPLETE`

### 9. Verify A Seat Is Marked Booked

```bash
curl --location "$BASE_URL/api/v1/seat-inventories/7"
```

Expected result:

- seat inventory status `BOOKED`

## Optional: Create New Data Instead Of Using Seeded Data

If a reviewer wants to validate create APIs directly, these are simple starter examples.

### Create User

```bash
curl --location --request POST "$BASE_URL/api/v1/users" \
  --header "Content-Type: application/json" \
  --data '{
    "name": "Integration User",
    "email": "integration.user@example.com",
    "mobileNumber": "+919900000001",
    "password": "CorrectHorse1"
  }'
```

### Create Event

```bash
curl --location --request POST "$BASE_URL/api/v1/events" \
  --header "Content-Type: application/json" \
  --data '{
    "name": "Test Event",
    "description": "Assignment demo event",
    "eventType": "CONCERT",
    "pricingType": "CATEGORY"
  }'
```

### Create Event Show

```bash
curl --location --request POST "$BASE_URL/api/v1/event-shows" \
  --header "Content-Type: application/json" \
  --data '{
    "eventId": 1,
    "venueId": 1,
    "startTime": "2027-01-01T18:00:00Z",
    "endTime": "2027-01-01T22:00:00Z"
  }'
```

## Suggested Review Approach

If you are evaluating this assignment, the fastest path is:

1. run the app with the `stage` profile
2. open Swagger UI
3. run `mvn test`
4. execute the happy flow in this README
5. inspect the concurrency integration test for overbooking protection

## Future Improvements

- add a Postman collection or `.http` request file for one-click API review
- add Docker Compose for PostgreSQL and app startup
- add CI workflow for build and test automation
