# es-host-service

A small **Spring Boot app** that uses **es-client** to run **search** and **index** requests against Elasticsearch. You can point each ES version (8.5, 8.13, 9.1) at different clusters or the same one. Good for trying out the client or as a thin API in front of ES.

---

## What it does

- Exposes two HTTP APIs: **search** and **index** (bulk).
- Every request says which **ES version** to use (`8.5`, `8.13`, or `9.1`). Each version can have its own **hosts, username, password, and socket timeout** in config.
- On startup, service reads enabled versions, registers the matching repository beans in `EsRepositoryFactory`, creates version clients via repository `createClient(...)`, and caches them in host service memory.
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

## Spinning up Elasticsearch with Docker (optional)

If Docker is installed, you can run Elasticsearch in containers and point the service at them. Example: ES 8.5.3 on **9200**, 8.13.4 on **9201**, 9.1.1 on **9202** (all with security enabled, password `password`).

**ES 8.5.3 (port 9200):**

```bash
docker run -d \
  --name es8-5-3 \
  -p 9200:9200 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=true" \
  -e "ELASTIC_PASSWORD=password" \
  -e "ES_JAVA_OPTS=-Xms500m -Xmx500m" \
  docker.elastic.co/elasticsearch/elasticsearch:8.5.3
```

**ES 8.13.4 (port 9201):**

```bash
docker run -d \
  --name es8-13-4 \
  -p 9201:9200 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=true" \
  -e "ELASTIC_PASSWORD=password" \
  -e "ES_JAVA_OPTS=-Xms500m -Xmx500m" \
  docker.elastic.co/elasticsearch/elasticsearch:8.13.4
```

**ES 9.1.1 (port 9202):**

```bash
docker run -d \
  --name es9-1-1 \
  -p 9202:9200 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=true" \
  -e "ELASTIC_PASSWORD=password" \
  -e "ES_JAVA_OPTS=-Xms500m -Xmx500m" \
  docker.elastic.co/elasticsearch/elasticsearch:9.1.1
```

Then in config set `hosts`, `username` (e.g. `elastic`), and `password` per version (e.g. `localhost:9200` for 8.5, `localhost:9201` for 8.13, `localhost:9202` for 9.1).

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
        username: elastic      # leave blank if no auth
        password: password
        socket-timeout: 60000
      8.13:
        hosts: localhost:9201
        username: elastic
        password: password
        socket-timeout: 60000
      9.1:
        hosts: localhost:9202
        username: elastic
        password: password
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

## Prepare Elasticsearch test data (direct ES cURLs)

Before calling `/api/v1/es/search`, you can create a sample index and insert test documents directly in Elasticsearch. When using the Docker setup above (security enabled), use username **elastic** and password **password** with `-u elastic:password`. Adjust the port (e.g. 9200, 9201, 9202) to match the ES instance you use.

### 1) Create index

```bash
curl -u elastic:password -X PUT "http://localhost:9200/test_index" \
  -H "Content-Type: application/json" \
  -d '{
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 1
    },
    "mappings": {
      "properties": {
        "name": { "type": "keyword" },
        "age":  { "type": "integer" },
        "created_at": { "type": "date" }
      }
    }
  }'
```

### 2) Ingest a few sample documents

```bash
curl -u elastic:password -X POST "http://localhost:9200/test_index/_bulk" \
  -H "Content-Type: application/x-ndjson" \
  -d '
{ "index": { "_id": "1" } }
{ "name": "alice", "age": 28, "created_at": "2026-01-15T10:00:00Z" }
{ "index": { "_id": "2" } }
{ "name": "bob", "age": 34, "created_at": "2026-01-16T11:30:00Z" }
{ "index": { "_id": "3" } }
{ "name": "charlie", "age": 25, "created_at": "2026-01-17T09:15:00Z" }
'
```

### 3) Verify docs are searchable in ES

```bash
curl -u elastic:password -X GET "http://localhost:9200/test_index/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match_all": {}
    }
  }'
```

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

Search response now includes `metadata` per document. This field is a stringified JSON of the ES `_source` fields returned for each hit.

Example response shape:

```json
{
  "documents": [
    {
      "entityId": "1",
      "metadata": "{\"name\":\"alice\",\"age\":28,\"created_at\":\"2026-01-15T10:00:00Z\",\"_version\":1}"
    }
  ],
  "resultCount": 1
}
```

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
