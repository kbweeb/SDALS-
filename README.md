# Smart Door Access Control System

A MOES ZigBee smart lock, managed locally through a SONOFF USB coordinator, Zigbee2MQTT,
Mosquitto, a Spring Boot API, PostgreSQL and an ASP.NET Core MVC admin dashboard.
Full design: [docs/](docs/).

## Layers and folders

Each software layer has its own top-level folder. Each layer only talks to the layer next to it.

```
 Browser
   │  HTTPS
 frontend/      ASP.NET Core MVC (C#)      admin dashboard          :5010 / :7078
   │  REST + JWT (Bearer)
 backend/       Spring Boot (Java 25)      business logic, auth     :8080
   │  JPA                    │  MQTT
 database/      PostgreSQL   messaging/    Mosquitto broker         :5432 / :1883
                               │  MQTT  (zigbee2mqtt/smart_lock, .../set)
                             zigbee2mqtt/  ZigBee <-> MQTT bridge   :8081 (its own UI)
                               │  USB serial
                             SONOFF ZBDongle-P  ))) ZigBee )))  MOES lock   (hardware)
```

| Folder | Layer | What's in it |
|---|---|---|
| `frontend/` | Presentation | MVC app. `Controllers/` → `Services/BackendApiClient.cs` → backend. No direct DB or MQTT access. |
| `backend/` | Application / API | Spring Boot app. See its internal layers below. |
| `database/` | Persistence | `init/*.sql`, the schema. It runs automatically when the postgres container is first created. |
| `messaging/` | Messaging | `mosquitto.conf` for the MQTT broker. |
| `zigbee2mqtt/` | Protocol bridge | `data/configuration.yaml` for Zigbee2MQTT. |
| `docs/` | — | Technical documentation and presentation. |
| `docker-compose.yml` | — | Runs the database, messaging and bridge layers. |

### Backend internal layers (`backend/src/main/java/com/cardsystem/smartdoor/`)

| Package | Role | May call |
|---|---|---|
| `controller/` | REST endpoints, HTTP only | `service` |
| `service/` | Business rules, transactions | `repository`, `mqtt.LockGateway` |
| `repository/` | Spring Data JPA | database |
| `model/` | JPA entities and enums | — |
| `dto/` | API request/response records | — |
| `mqtt/` | `LockGateway` (outbound commands) and `MqttInboundHandler` (telemetry in → services) | `service` (inbound only) |
| `security/`, `config/` | JWT, Spring Security, first-admin bootstrap | — |

### REST API (all `/api/**` need an ADMIN JWT except login)

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/auth/login` | `{username, password}` → `{token, expiresInSeconds}` |
| GET/POST | `/api/users` | list / create users |
| GET/PUT | `/api/users/{id}` | get / update |
| POST | `/api/users/{id}/deactivate` | deactivate (never deleted, keeps audit trail) |
| GET | `/api/users/{id}/credentials` | credential slots for a user |
| POST | `/api/credentials` | map a lock slot (fingerprint/RFID/PIN index) to a user |
| DELETE | `/api/credentials/{id}` | remove a mapping |
| GET | `/api/device/status` | last reported lock state, battery, link quality |
| POST | `/api/device/unlock`, `/api/device/lock` | remote command (if the firmware supports it) |
| GET | `/api/access-logs?page=&size=` | newest audit entries first |

## Running it (development)

1. Infrastructure (Docker Desktop):
   ```
   docker compose up -d postgres mosquitto
   ```
2. Backend:
   ```
   cd backend
   .\mvnw spring-boot:run
   ```
   On first start it creates an admin `admin` / `admin123` (override with `ADMIN_USER` / `ADMIN_PASSWORD`).
3. Frontend: open `CARD system.slnx` in Visual Studio and run, or `dotnet run --project frontend`.

## Known open items

- **Zigbee2MQTT and the USB dongle on Windows.** Docker Desktop can't pass a USB serial device into a container directly. Either attach the dongle to WSL2 with [usbipd-win](https://github.com/dorssel/usbipd-win), or run Zigbee2MQTT natively (Node.js) and point it at the COM port. Decide this in Stage 2.
- **Lock payload format is provisional.** `MqttLockGateway` and `AccessLogService.recordFromTelemetry` assume common Zigbee2MQTT field names (`state`, `battery`, `linkquality`, `action`, `action_user`). Confirm them in Stage 3 by watching the `zigbee2mqtt/smart_lock` topic while using the lock.
- **Mosquitto allows anonymous connections.** That's fine on a local prototype, but turn it off before real use.
