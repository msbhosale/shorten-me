# Changelog

This file tracks the project state that already existed in the repository and the changes added on 2026-03-20.

## Existing Before 2026-03-20

### Core application

- Spring Boot URL shortener API built with Java 17.
- Creates a 6-character short code for a long URL and `userId`.
- Stores URL mappings in MySQL.
- Uses Redis for lookup caching.
- Exposes Spring Boot Actuator and Prometheus endpoints for monitoring.

### Available endpoints

- `POST /api/v1/urls` to create a short URL.
- `GET /api/v1/urls/{shortCode}` to fetch short URL details.
- `GET /{shortCode}` to redirect to the original URL.

### Existing behavior

- Duplicate requests for the same `originalUrl` and `userId` return the existing short URL.
- Short URL responses include short code, original URL, shortened URL, created time, and expiry time.
- Expiry was set to 10 days from creation.
- Redirects worked when a short code existed.
- Structured `404` handling already existed for missing URLs in the API response flow.

## Added On 2026-03-20

### Validation and request safety

- Added `spring-boot-starter-validation` to the project.
- Added validation rules on `ShortUrlRequest`.
- `originalUrl` must not be blank.
- `originalUrl` must be between 10 and 2048 characters.
- `originalUrl` must start with `http://` or `https://`.
- `userId` must not be null.
- `userId` must be a positive number.
- Enabled `@Valid` validation on the create URL API endpoint.
- Added validation error handling in `GlobalExceptionHandler` to return `400 Bad Request`.

### New and updated API behavior

- Added `GET /api/v1/urls?userId=...` to fetch all short URLs created by a user.
- Added repository support with `findByUserId(Long userId)`.
- Changed redirect handling to use service-level exceptions instead of returning an empty optional.
- Added expiry checks for both detail lookup and redirect flow.
- Added `410 Gone` handling for expired short URLs through `UrlExpiredException`.

### URL lifecycle updates

- Updated default expiry from 10 days to 365 days in `ShortUrl.prePersist()`.
- Added `isExpired()` helper method on the `ShortUrl` entity.
- Ensured shortened URLs are built as `baseUrl + "/" + shortCode`.
- Updated application config to store base URL without a trailing slash.

### Documentation and tests

- Expanded service and repository coverage around the new behavior.
- Updated README wording to reflect the latest API behavior and setup notes.
- Added this `CHANGELOG.md` file to document previous functionality and today's additions.
