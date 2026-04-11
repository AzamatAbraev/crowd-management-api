# Crowd Management API

The core backend service. It subscribes to MQTT telemetry from IoT sensors, maintains real-time occupancy counts, stores historical data in InfluxDB, exposes a REST API consumed by the gateway, and pushes live updates to connected frontends.

## Responsibilities

- Receive and process MQTT messages from ultrasonic sensors
- Track live people count per room, floor, and building
- Persist time-series telemetry to InfluxDB for historical queries and Grafana dashboards
- Manage the device registry (online/offline status, firmware version, last heartbeat)
- Manage users via the Keycloak Admin API
- Serve timetable data parsed from an aSc Timetables XML export
- Provide analytics endpoints backed by InfluxDB queries

## Stack

- Java 21, Spring Boot 4.0.1
- Spring Security (OAuth2 Resource Server, JWT)
- Spring Integration MQTT (Eclipse Paho v3 client)
- InfluxDB 3 Core (`influxdb3-java` client)
- Keycloak Admin Client 26.x (user management)
- Lombok

## Architecture

The API runs as an OAuth2 resource server. It validates JWT tokens issued by Keycloak and extracts roles using a custom `KeycloakRoleConverter`. All endpoints require authentication. User management endpoints additionally require the `theking` role.

The MQTT client is configured as a Spring Bean in `MqttConfig`. On startup it connects to the broker and subscribes to two wildcard topics:

- `wiut/+/+/+/+/+/+/telemetry` — occupancy delta events
- `wiut/+/+/+/+/+/+/status` — device birth/death events

Telemetry processing:
1. Validates the payload schema version (`v` field)
2. Detects sequence gaps to identify lost messages
3. Ignores misfire events (but still records the device heartbeat)
4. Updates the live people count via `PeopleCountService`
5. Persists the event to InfluxDB via `InfluxDbTelemetryService`
6. Updates device last-seen timestamp and firmware version

## Ports

| Service | Port |
|---|---|
| API | 8081 |
| InfluxDB | 8181 |
| Grafana | 3000 |

## Running with Docker

The project uses a multi-stage Docker build. Maven runs inside the build stage — no local Java or Maven installation is needed.

```bash
docker compose up -d
```

All three services (API, InfluxDB, Grafana) are defined in `docker-compose.yml` and share the `crowd-management-network` Docker network.

## Dependencies on other services

The API container expects the following to be reachable on the shared Docker network before it starts:

| Dependency | Docker container name | Purpose |
|---|---|---|
| Keycloak | `keycloak-app` | JWT validation, user management |
| Mosquitto | `mosquitto` | MQTT telemetry ingestion |
| InfluxDB | `influxdb` | Telemetry persistence |

Start order is managed by `start-all.sh` in the root of the project.

## Configuration

Key settings in `application.yml` (overridable via environment variables in `docker-compose.yml`):

| Setting | Value | Notes |
|---|---|---|
| `mqtt.broker-url` | `tcp://mosquitto:1883` | Docker container name |
| `mqtt.telemetry-topic` | `wiut/+/+/+/+/+/+/telemetry` | Wildcard subscription |
| `mqtt.status-topic` | `wiut/+/+/+/+/+/+/status` | Wildcard subscription |
| `influxdb.url` | `http://influxdb:8181` | InfluxDB 3 Core, no auth |
| `influxdb.database` | `telemetry` | |
| `keycloak.server-url` | `http://keycloak-app:8080` | Admin API base URL |
| `keycloak.admin-client-id` | `crowd-management-api` | Service account client |
| `keycloak.realm` | `crowd-management` | |

## Timetable

The API serves timetable data from `src/main/resources/timetable.xml`, which is an aSc Timetables 2026 XML export. This file is volume-mounted in `docker-compose.yml` so it can be updated without rebuilding the image.

## Grafana

Grafana is included in the same `docker-compose.yml`. It connects to InfluxDB as a datasource and is configured for SSO via Keycloak OAuth2. Users with the `theking` role are assigned `GrafanaAdmin`; all others receive `Viewer`.

The Grafana sign-out redirects through the gateway logout endpoint (`http://localhost:8082/logout`) to ensure the Keycloak session is also terminated.
