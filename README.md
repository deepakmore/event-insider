# Event Insider

`Event Insider` is a multi-module Spring Boot project for managing events, shows, seats, inventory, bookings, and payment confirmation callbacks, the easiest way to review it is:

1. run the application
2. run the automated tests
3. try the sample API happy flow below ( Refer docs\pre-requisites.md and happy_flows.md)

## Tech Stack

- Java 20
- Spring Boot 4
- Spring MVC
- Spring Data JPA
- Flyway
- PostgreSQL
- Maven

## Project Structure

- `event-insider-common`: shared DTOs, enums, exceptions
- `event-insider-persistence`: entities, repositories, Flyway migrations
- `event-insider-services`: business logic
- `event-insider-application`: controllers, filters, app bootstrap, integration tests

## Key Features

### Functional
- user registration and retrieval
- event and show management
- event seat category pricing
- seat inventory tracking
- booking creation and cancellation

### Technical
- payment webhook confirmation
- concurrent booking test coverage
- slack alerts for critical Flow
- caching for events data
- capture requestId and userId in each log

## Assumptions:
- This application currently focuses on `concert` events. 
- Each concert is assumed to have one show per day. 
- Pricing is fixed (flat); dynamic pricing is out of scope. 
- Bulk operations not available

## Run -
- start the API normally
- inspect the seeded data created by Flyway
- run the integration tests
- execute the happy-flow API calls from this README

