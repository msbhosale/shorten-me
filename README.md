# Shorten Me API

`shorten-me` is a Spring Boot URL shortener API. It accepts a long URL and a `userId`, generates a 6-character short code, stores the mapping in MySQL, and exposes endpoints to:

- create a short URL
- fetch short URL details by short code
- redirect a short code to the original URL

The service also uses Redis caching for URL lookups and exposes Spring Boot Actuator endpoints.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- MySQL
- Redis
- Spring Actuator
- Micrometer Prometheus

## Default Configuration

Configured in [`application.yml`]:

- App port: `8085`
- Base URL: `http://localhost:8085/`
- MySQL database: `jdbc:mysql://localhost:3306/url_shortener`
- Redis host: `localhost:6379`

Base API URL:

```text
http://localhost:8085/api/v1
```

## What This API Does

When a client sends an original URL with a `userId`:

1. The service creates a SHA-256 hash using `originalUrl + userId`.
2. If the same user already shortened the same URL earlier, the existing record is returned.
3. Otherwise, a new 6-character Base62 short code is generated and saved.
4. The response includes the original URL, short code, shortened URL, creation timestamp, and expiry timestamp.

Important behavior:

- Short code length is `6`
- Expiry is automatically set to `createdAt + 10 days`
- Duplicate shortening for the same `originalUrl` and `userId` returns the existing short URL
- API lookup `/api/v1/urls/{shortCode}` returns JSON
- Browser-style lookup `/{shortCode}` returns an HTTP redirect

## Request and Response Models

### Create Short URL Request

```json
{
  "originalUrl": "https://example.com/some/very/long/path",
  "userId": 101
}
```

Fields:

- `originalUrl` (`string`, required): the full URL to shorten
- `userId` (`number`, required): identifier of the user creating the short URL

### Short URL Response

```json
{
  "shortCode": "aZ19kP",
  "originalUrl": "https://example.com/some/very/long/path",
  "createdAt": "2026-03-19T10:15:30",
  "expiryAt": "2026-03-29T10:15:30",
  "shortenedUrl": "http://localhost:8085/aZ19kP"
}
```

Fields:

- `shortCode` (`string`): generated short identifier
- `originalUrl` (`string`): original long URL
- `createdAt` (`datetime`): timestamp when record was created
- `expiryAt` (`datetime`): expiry timestamp, currently 10 days after creation
- `shortenedUrl` (`string`): complete redirect URL using `app.short-url.base-url`

### Error Response

```json
{
  "message": "Url with the code abc123 not found",
  "status": 404
}
```

## API Reference

### 1. Create Short URL

**Endpoint**

```http
POST /api/v1/urls
```

**Description**

Creates a new shortened URL for a given original URL and user. If the same user already shortened the same URL, the existing mapping is returned.

**Request Body**

```json
{
  "originalUrl": "https://openai.com/research",
  "userId": 1
}
```

**Success Response**

- Status: `201 Created`

```json
{
  "shortCode": "x7Qa2B",
  "originalUrl": "https://openai.com/research",
  "createdAt": "2026-03-19T11:00:00",
  "expiryAt": "2026-03-29T11:00:00",
  "shortenedUrl": "http://localhost:8085/x7Qa2B"
}
```

**cURL**

```bash
curl -X POST http://localhost:8085/api/v1/urls \
  -H "Content-Type: application/json" \
  -d '{
    "originalUrl": "https://openai.com/research",
    "userId": 1
  }'
```

### 2. Get Short URL Details

**Endpoint**

```http
GET /api/v1/urls/{shortCode}
```

**Description**

Returns the stored metadata for a short code as JSON. This endpoint uses caching for lookups.

**Path Parameter**

- `shortCode` (`string`, required): the short code to fetch

**Success Response**

- Status: `200 OK`

```json
{
  "shortCode": "x7Qa2B",
  "originalUrl": "https://openai.com/research",
  "createdAt": "2026-03-19T11:00:00",
  "expiryAt": "2026-03-29T11:00:00",
  "shortenedUrl": "http://localhost:8085/x7Qa2B"
}
```

**Not Found Response**

- Status: `404 Not Found`

```json
{
  "message": "Url with the code x7Qa2B not found",
  "status": 404
}
```

**cURL**

```bash
curl http://localhost:8085/api/v1/urls/x7Qa2B
```

### 3. Redirect to Original URL

**Endpoint**

```http
GET /{shortCode}
```

**Description**

Redirects the client to the original URL for the given short code.

**Path Parameter**

- `shortCode` (`string`, required): the short code to resolve

**Success Response**

- Status: `307 Temporary Redirect`
- Header: `Location: <originalUrl>`

Example:

```http
HTTP/1.1 307 Temporary Redirect
Location: https://openai.com/research
```

**Not Found Response**

- Status: `404 Not Found`
- Empty response body

**cURL**

```bash
curl -i http://localhost:8085/x7Qa2B
```

## Endpoint Summary

| Method | URL                        | Purpose                                | Success Status           |
| ------ | -------------------------- | -------------------------------------- | ------------------------ |
| POST   | `/api/v1/urls`             | Create or return an existing short URL | `201 Created`            |
| GET    | `/api/v1/urls/{shortCode}` | Fetch short URL details as JSON        | `200 OK`                 |
| GET    | `/{shortCode}`             | Redirect to the original URL           | `307 Temporary Redirect` |

## Actuator and Monitoring

Because Actuator exposure is enabled with `include: "*"`, management endpoints are available locally, including:

- `/actuator/health`
- `/actuator/prometheus`

Example:

```bash
curl http://localhost:8085/actuator/health
```

## Running Locally

### Prerequisites

- Java 17
- Maven
- MySQL running on `localhost:3306`
- Redis running on `localhost:6379`

### Steps

1. Create a MySQL database named `url_shortener`.
2. Update database credentials in [`src/main/resources/application.yml`](/Users/msbhosale/Documents/MS Bhosale/Gitlab-Repos/shorten-me/src/main/resources/application.yml) if needed.
3. Start MySQL and Redis.
4. Run the application:

```bash
./mvnw spring-boot:run
```

The application starts on:

```text
http://localhost:8085
```

## Notes and Limitations

- There is currently no request validation annotation on the input DTO, so invalid or blank URLs are not explicitly rejected by validation rules.
- Redirect lookup does not currently check `expiryAt` or `isActive`; it redirects whenever the short code exists.
- The redirect endpoint returns an empty `404` body, while the JSON lookup endpoint returns a structured error body.
- There is no generated Swagger/OpenAPI UI in the project right now; this README acts as the API reference based on the current implementation.
