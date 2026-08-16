# Smart Notification Management System

A Spring Boot service for creating, listing, retrying, and monitoring notifications
(EMAIL / SMS / PUSH) with async dispatch and simulated delivery failure.

## 1. Setup Steps

### Prerequisites
- JDK 17+
- Maven 3.8+
- MySQL 8+ running locally

### Database
```sql
CREATE DATABASE notification_db;
```
The app also creates/updates using `ddl-auto: update` the `notifications` table automatically.

### Configure
Edit `src/main/resources/application.yml` if your MySQL username differ from
`root` and enter your passworf in place of `{PASSWORD}`.

### Run
```bash
mvn spring-boot:run
```
App starts on `http://localhost:8080`.

### Sample requests
```bash
# Create
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"userId":101,"type":"EMAIL","message":"First Notification","scheduleTime":"2026-08-16T10:50:00"}'

# List (with filters + pagination)
curl "http://localhost:8080/api/notifications?status=FAILED&type=EMAIL&page=0&size=10"

# Retry
curl -X POST http://localhost:8080/api/notifications/1/retry

# Dashboard
curl http://localhost:8080/api/dashboard
```

---

## 2. Architecture

```
Controller  ->  Service  ->  Repository  ->  DB
                  |
                  v
          NotificationQueueService (in-memory BlockingQueue)
                  |
                  v
          NotificationDispatchWorker (background threads)
                  |
                  v
          NotificationDispatcher (simulated send + 30% random failure)
```

- **Controller layer**: thin - only handles HTTP concerns and DTO (de)serialization.
- **Service layer**: owns all business rules (duplicate check, message validation,
  retry eligibility). This is deliberately where the "interesting" logic lives, so it's
  independently testable without spinning up the web layer.
- **Repository layer**: Spring Data JPA.
- **Async dispatch**: notification creation/retry only validates + persists on the
  request thread, then hands the id off to an in-memory queue. A small worker pool
  picks it up and randomly marks it `SENT` or `FAILED`
  (~30% failure rate) - isolated in one class (`NotificationDispatcher`).

### Why in-memory queue instead of RabbitMQ
RabbitMQ was optional in the brief. Given the time-boxed nature of the assessment,
an in-memory `BlockingQueue` satisfies the "asynchronous processing" requirement with no
infra dependency, while keeping the producer/consumer boundary in the same shape it would
need to be for RabbitMQ.

No other code would need to change.

---

## 3. Database Schema

**Table: `notifications`**

| Column | Type | Notes                      |
|---|---|----------------------------|
| id | BIGINT (PK, auto increment) |                            |
| user_id | BIGINT |                            |
| type | VARCHAR (enum: EMAIL/SMS/PUSH) | indexed                    |
| message | TEXT |                            |
| status | VARCHAR (enum: PENDING/SENT/FAILED/RETRYING) | indexed                    |
| schedule_time | DATETIME |                            |
| retry_count | INT, default 0 |                            |
| last_retry_at | DATETIME, nullable |                            |
| created_at | DATETIME | set on insert              |
| updated_at | DATETIME | set on every insert/update |

**Indexes**
- `idx_status` on `status` - dashboard aggregation + retry-eligibility lookups filter by status often.
- `idx_type` on `type` - dashboard type-wise stats.

`retry_count` and `last_retry_at` live directly on the notification row rather than a
separate table, since the requirement only needs count of retries and time of
last retry, not full retry history.

---

## 4. Assumptions Made

- **Retry cooldown** is measured from `last_retry_at`, i.e. "2 minutes since the *last*
  retry attempt" rather than since the original failure.
- **Duplicate window** is a rolling 5 minutes from `created_at` of the prior matching
  notification, not a fixed time bucket (e.g. not "same 5-minute clock window").
- **Message repetition rule** is case-insensitive and strips punctuation before counting
  (`"Hello, hello!"` counts as two occurrences of `hello`).
- **Random failure simulation** only applies at the dispatch/send step (async), not to
  the retry-eligibility decision itself - retries are re-dispatched through the same
  simulated-send path.
- **Schedule time in create request** is assumed as the time of creation of the
  notification and not the time at which the notification becomes eligible for processing.

---

## 5. Important Implementation Details

- **Validation** is layered: Bean Validation (`@Valid`) on the DTO for null/blank checks.
- **Exception handling** is centralized in `GlobalExceptionHandler` (`@RestControllerAdvice`):
  - 404 - notification not found
  - 409 - duplicate notification
  - 400 - invalid message / retry not eligible / bean validation failures
  - 500 - fallback for anything unhandled
- **DTOs** are used at every API boundary - entities are never returned directly.
- **Logging** (SLF4J) is at INFO for lifecycle events (created, enqueued, dispatched,
  sent, retried) and WARN/ERROR for rejections and failures.
- Retry eligibility (`FAILED` status + `retryCount < 3` + cooldown elapsed) and the
  duplicate check are both implemented in
  `NotificationServiceImpl` rather than inline in the controller.

---
