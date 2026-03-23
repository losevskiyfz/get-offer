# Get Offer

A microservice-based system for candidate salary calculation and offer management.

## Architecture

| Service | Port | Description |
|---|---|---|
| candidate-service | 8080 | Manages candidates, publishes events |
| calculation-service | 8081 | Consumes candidate events, calculates recommended salary |
| offer-service | 8082 | Consumes calculation events, stores and serves offers |

## Prerequisites

- Docker
- Docker Compose

## Running locally
```bash
docker compose -f docker-compose.infra.yml -f docker-compose.yml up -d
```

To stop:
```bash
docker compose -f docker-compose.infra.yml -f docker-compose.yml down
```

## API

### candidate-service — [Swagger UI](http://localhost:8080/swagger-ui/index.html)

| Method | URL | Description |
|---|---|---|
| `POST` | `/api/v1/candidates` | Create a candidate |
| `GET` | `/api/v1/candidates/{id}` | Get a candidate by ID |

Example request:
```json
POST /api/v1/candidates
{
  "name": "Ivan",
  "grade": "JUNIOR",
  "experienceYears": 0,
  "salary": 400
}
```

### offer-service — [Swagger UI](http://localhost:8082/swagger-ui/index.html)

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/v1/offers` | Get all offers (paginated) |
| `GET` | `/api/v1/offers/candidates/{candidateId}` | Get latest offer for a candidate |

Example request:
```
GET /api/v1/offers?page=0&size=20&sort=createdAt,desc
```

## Flow

1. Create a candidate via `POST /api/v1/candidates`
2. `calculation-service` automatically calculates the recommended salary
3. `offer-service` stores the result — retrieve it via `GET /api/v1/offers`