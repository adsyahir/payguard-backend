# PayGuard Backend

Microservices payment platform with AI fraud detection — **Spring Cloud** pattern.
Monorepo, Maven multi-module. **Java 21** · **Spring Boot** · **Spring Cloud**.

See the full spec in `../docs/PayGuard-Spec.md` (or the pay-guard/docs folder).

---

## Modules

| Module | Port | Role | Status |
|--------|------|------|--------|
| `infra/config-server` | 8888 | Spring Cloud Config Server — serves `config-repo/` | ✅ wired |
| `infra/discovery-server` | 8761 | Netflix Eureka service discovery | ✅ wired |
| `services/api-gateway` | 8080 | Spring Cloud Gateway — single entry, `lb://` routing, JWT | ✅ wired |
| `services/user-service` | 8081 | Registration, auth (JWT), merchant profiles, RBAC | 🚧 wiring done, domain WIP |
| `services/payment-service` | 8082 | Stripe, charges, refunds, webhooks | ⬜ not created |
| `services/fraud-engine` | 8083 | Real-time scoring, ML, rule fallback (Redis) | ⬜ not created |
| `services/notification-service` | 8084 | Email/SMS alerts | ⬜ not created |
| `services/reconciliation-service` | 8085 | Settlement matching, reporting | ⬜ not created |

---

## Spring Cloud wiring

- **Config Server** holds all service config in `infra/config-server/src/main/resources/config-repo/`.
  Each service pulls it via `spring.config.import=optional:configserver:...`.
  Config files are named after the service (`user-service.yml`, `api-gateway.yml`, …).
  A shared `application.yml` there holds common defaults (Eureka URL).
- **Eureka** — every service registers on boot (eureka-client dependency, no annotation).
  Gateway routes with `lb://<service-id>`; services call each other by id, never a hardcoded URL.
- **Database-per-service** — one Postgres instance, five databases (`infra/postgres-init.sql`).

### Server vs client (the key rule)
- Infra apps = **server** starters + an `@Enable*`:
  `config-server` → `@EnableConfigServer`, `discovery-server` → `@EnableEurekaServer`.
- Business services = **client** starters, no annotation:
  `spring-cloud-starter-config` + `spring-cloud-starter-netflix-eureka-client`.

---

## Configuration — `.env`

One `.env` at the repo root feeds **both** docker-compose and the services.

- **docker-compose** auto-reads `.env` (Postgres user/password).
- **services** read it via [spring-dotenv]; `dotenv.directory: ../..` in each service points here.
  Placeholders `${VAR}` in the config-repo yml resolve from `.env` (with safe defaults).

```bash
cp .env.example .env      # then edit secrets (JWT_SECRET, Stripe keys, …)
```
`.env` is gitignored. `.env.example` is the committed template.

---

## Run locally

### 1. Infrastructure (Docker)
```bash
docker compose up -d postgres        # user-service only needs Postgres
# docker compose up -d               # everything (Postgres, Redis, Kafka, Mailpit) — later
```

### 2. Services — order matters (config + discovery first)
Each in its own terminal, from its own folder:
```bash
cd infra/config-server    && ./mvnw spring-boot:run   # :8888
cd infra/discovery-server && ./mvnw spring-boot:run   # :8761
cd services/api-gateway   && ./mvnw spring-boot:run   # :8080
cd services/user-service  && ./mvnw spring-boot:run   # :8081  (loads ../../.env)
```

### 3. Verify
| Check | URL |
|-------|-----|
| Eureka dashboard (services `UP`) | http://localhost:8761 |
| Config for user-service | http://localhost:8888/user-service/default |
| Gateway health | http://localhost:8080/actuator/health |
| Mailpit UI (later) | http://localhost:8025 |

---

## Build

```bash
./mvnw -DskipTests package     # from a module folder — build its jar
./mvnw -DskipTests compile     # quick compile check
```

---

## Ports

| Service | Port |  | Infra | Port |
|---------|------|--|-------|------|
| api-gateway | 8080 |  | Postgres | 5432 |
| user-service | 8081 |  | Redis | 6379 |
| payment-service | 8082 |  | Kafka | 9092 |
| fraud-engine | 8083 |  | Mailpit SMTP | 1025 |
| notification-service | 8084 |  | Mailpit UI | 8025 |
| reconciliation-service | 8085 |  | Config Server | 8888 |
|  |  |  | Eureka | 8761 |

---

## Next steps

Currently: infra wired + 4 modules. Building **user-service** domain first.

- [ ] user-service: `V1__init.sql` (merchants, users, roles, user_roles) → entities → repos
- [ ] user-service: security + JWT (BCrypt, resource-server) → register/login → RBAC endpoints
- [ ] scaffold remaining 4 services + their `config-repo/*.yml` + gateway routes
- [ ] domain code per service (Stripe, fraud scoring, Kafka events, reconciliation job)
- [ ] align root pom / child pom Boot versions; add CI

Requires JDK 21+, Docker + Docker Compose.

[spring-dotenv]: https://github.com/paulschwarz/spring-dotenv
