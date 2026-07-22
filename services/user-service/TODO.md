# user-service — Build TODO

Build order. Do top to bottom. Don't skip ahead — each step unblocks the next.

## ✅ Done
- [x] Module scaffold (pom, client deps, spring-dotenv)
- [x] application.yml (name + config import) + config-repo/user-service.yml
- [x] Flyway migration V1 — merchants, users, roles, permissions, user_roles, role_permissions, refresh_tokens
- [x] docker-compose (Postgres :5433)

## ✅ Milestone 1 — it boots + connects
- [x] `docker compose up -d postgres`
- [x] Run config-server, then user-service
- [x] Confirm: Flyway creates tables, Hikari connects, no errors

## 🎯 Milestone 2 — data layer
- [x] Entities: `Merchant`, `User`, `Role`, `Permission`, `RefreshToken`
- [x] Repositories: `UserRepo` (findByEmail), `MerchantRepo`, `RoleRepo` (findByName),
      `PermissionRepo`, `RefreshTokenRepo`
- [ ] Run app — confirm it boots with `ddl-auto: validate` (entities match tables)

## 🎯 Milestone 3 — auth core (THE important one)
- [ ] `PasswordEncoder` bean (BCrypt)
- [ ] `SecurityFilterChain` — permit `/api/auth/**`, protect the rest
- [ ] JWT: `JwtEncoder` + `JwtDecoder` (HMAC / shared secret from .env)
- [ ] `POST /api/auth/register` — create merchant + admin user (hash password)
- [ ] `POST /api/auth/login` — verify password → return access JWT
- [ ] Test with curl: register → login → get token

## 🎯 Milestone 4 — protected + refresh
- [ ] `GET /api/users/me` — read current user from JWT
- [ ] `POST /api/auth/refresh` — refresh token → new access token (rotate)
- [ ] `POST /api/auth/logout` — revoke refresh token
- [ ] Test: call /me with token (200), without token (401)

## 🎯 Milestone 5 — RBAC (do LAST)
- [ ] Seeder class (permissions → roles) — idempotent ApplicationRunner
- [ ] Put roles/permissions into JWT claims
- [ ] `@PreAuthorize` on endpoints (e.g. hasAuthority('user:manage'))
- [ ] Test: role with permission passes, without = 403

## Later / cross-cutting
- [ ] Global exception handler (return ApiError)
- [ ] Input validation (@Valid on register/login DTOs)
- [ ] Integration tests (Testcontainers + Postgres)
- [ ] Wire gateway route /api/auth + /api/users → user-service

---
**Focus now: Milestone 1 → 2 → 3.** RBAC (M5) comes last. Don't build the seeder before login works.
