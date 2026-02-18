# es-host-service

A small **Spring Boot app** that uses **es-client** to run **search** and **index** requests against Elasticsearch. You can point each ES version (8.5, 8.13, 9.1) at different clusters or the same one. Good for trying out the client or as a thin API in front of ES.

---

## What it does

- Exposes two HTTP APIs: **search** and **index** (bulk).
- Every request says which **ES version** to use (`8.5`, `8.13`, or `9.1`). Each version can have its own **hosts, username, password, and socket timeout** in config.
- Uses the es-client library so the same code works for all supported versions.

---

## Setup

**Requirements:** Java 17, Maven.

1. **Build and install es-client** (this app depends on it):

   ```bash
   cd ../es-client
   mvn clean install
   ```

2. **Run the service:**

   ```bash
   cd ../es-host-service
   mvn spring-boot:run
   ```

   By default it runs on **port 8085**. To change: set `SERVER_PORT` (e.g. `SERVER_PORT=8080`).

---

## Config (how to point at your Elasticsearch)

Config is **per version**. In `application.yml` you’ll see something like:

```yaml
host:
  es:
    enabled-versions: 8.5,8.13,9.1   # which versions to turn on
    versions:
      8.5:
        hosts: localhost:9200
        username: ""          # leave blank if no auth
        password: ""
        socket-timeout: 60000
      8.13:
        hosts: localhost:9200
        username: ""
        password: ""
        socket-timeout: 60000
      9.1:
        hosts: localhost:9200
        username: ""
        password: ""
        socket-timeout: 60000
```

- **enabled-versions:** Comma-separated list of versions that should be registered (e.g. `8.5,8.13,9.1`).
- **versions.***version*: For each version you enable, set at least **hosts**. Optionally set **username**, **password**, and **socket-timeout** (milliseconds).

You can override with environment variables, for example:

- `ES_ENABLED_VERSIONS` – e.g. `8.5,8.13,9.1`
- `ES_V85_HOSTS`, `ES_V85_USERNAME`, `ES_V85_PASSWORD`, `ES_V85_SOCKET_TIMEOUT_MS`
- Same pattern for `ES_V813_*` and `ES_V91_*`

---

## API endpoints

Base path: **`/api/v1/es`**

| Method | Path        | What it does |
|--------|-------------|--------------|
| POST   | `/search`   | Run a search on the given index and ES version. |
| POST   | `/index`    | Bulk index documents into the given index and ES version. |

Each request body must include **version** (e.g. `"8.5"`) and **indexName**. Other fields (e.g. filters, limit) depend on the endpoint.

---

## Example: search

```bash
curl -X POST http://localhost:8085/api/v1/es/search \
  -H "Content-Type: application/json" \
  -d '{
    "version": "8.5",
    "indexName": "my-index",
    "searchRequest": {
      "filters": [],
      "searchQueries": [],
      "sorting": [],
      "boost": [],
      "responseFields": ["*"],
      "limit": 10,
      "offset": 0
    }
  }'
```

Change `"version"` to `"8.13"` or `"9.1"` to use that ES version (and its config).

---

## Example: index (bulk)

```bash
curl -X POST http://localhost:8085/api/v1/es/index \
  -H "Content-Type: application/json" \
  -d '{
    "version": "8.5",
    "indexName": "my-index",
    "indexRequest": {
      "documents": [
        { "id": "1", "title": "First doc" },
        { "id": "2", "title": "Second doc" }
      ]
    }
  }'
```

Again, set **version** to the ES version you want to use.

---

## Health check

If actuator is enabled (as in the default config), you can check:

```bash
curl http://localhost:8085/actuator/health
```

---

## Summary

| Step | Action |
|------|--------|
| 1 | Install es-client: `cd es-client && mvn clean install` |
| 2 | Configure `host.es.versions.<version>.hosts` (and optional username, password, socket-timeout) in `application.yml` or env. |
| 3 | Run: `cd es-host-service && mvn spring-boot:run` |
| 4 | Call `POST /api/v1/es/search` or `POST /api/v1/es/index` with `version` and `indexName` in the body. |
