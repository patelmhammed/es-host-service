# es-host-service

Minimal Spring Boot 3 / Java 17 host service to validate `msearch-es-client` behavior across ES versions (`8.5`, `8.13`).

## Prerequisites

From the sibling client repo, install artifacts locally:

```bash
cd ../msearch-es-client
mvn clean install
```

## Run

```bash
cd ../es-host-service
mvn spring-boot:run
```

Optional environment overrides:

- `ES_HOSTS` (comma-separated, default `localhost:9200`)
- `ES_SCHEME` (default `http`)
- `ES_AUTH_ENABLED` (`true`/`false`)
- `ES_USERNAME`, `ES_PASSWORD`
- `ES_CLUSTER_ID` (default `local`)

## Endpoints

- `GET /api/v1/es/ping`
- `POST /api/v1/es/search`
- `POST /api/v1/es/index/get`

## Sample curl requests

Ping wiring:

```bash
curl -s http://localhost:8080/api/v1/es/ping | jq
```

Search using ES `8.5`:

```bash
curl -s -X POST http://localhost:8080/api/v1/es/search \
  -H "Content-Type: application/json" \
  -d '{
    "version": "8.5",
    "indexName": "test-index",
    "searchRequest": {
      "filters": [],
      "searchQueries": [],
      "sorting": [],
      "boost": [],
      "responseFields": ["*"],
      "limit": 10,
      "offset": 0
    }
  }' | jq
```

Search using ES `8.13`:

```bash
curl -s -X POST http://localhost:8080/api/v1/es/search \
  -H "Content-Type: application/json" \
  -d '{
    "version": "8.13",
    "indexName": "test-index",
    "searchRequest": {
      "filters": [],
      "searchQueries": [],
      "sorting": [],
      "boost": [],
      "responseFields": ["*"],
      "limit": 10,
      "offset": 0
    }
  }' | jq
```

Get index metadata:

```bash
curl -s -X POST http://localhost:8080/api/v1/es/index/get \
  -H "Content-Type: application/json" \
  -d '{
    "version": "8.13",
    "indexName": "test-index"
  }' | jq
```
