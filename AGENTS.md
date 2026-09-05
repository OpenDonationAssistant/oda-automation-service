# AGENTS.md

Java 25 / Micronaut 5.1.0 backend service in the OpenDonationAssistant platform. Event-driven: consumes RabbitMQ events, evaluates automation rules (triggers → actions), persists to PostgreSQL, and exposes REST APIs for automation state and alerts.

## Build & Test

- **JDK 25 required** (`jdk.version=25` in `pom.xml`). Verify with `java -version`.
- Build: `./mvnw package`
- Test: `./mvnw test` — **tests use Testcontainers (PostgreSQL 14) via podman, NOT Docker.** The shell env already sets `DOCKER_HOST=unix:///run/user/1000/podman/podman.sock` and `TESTCONTAINERS_RYUK_DISABLED=true`; if those are missing, prefix the command with them.
- Single test: `./mvnw test -Dtest=ClassName` (e.g. `AutomationVariableRepositoryTest`).
- DB-backed tests use `@MicronautTest(environments = "allinone")`; the test datasource is `jdbc:tc:postgresql:14:///postgres` (Testcontainers JDBC URL).

## Compiler gates (build fails if violated)

- **NullAway is enforced as ERROR** with JSpecify mode (`-Xep:NullAway:ERROR -XepOpt:NullAway:JSpecifyMode=true`). Annotate nullability with `@Nullable`/`@NonNull` from `org.jspecify.annotations` (see `GetAlertsApi.listAlerts`). Generated sources are excluded.
- Error Prone runs with `-XDcompilePolicy=simple` and `--should-stop=ifError=FLOW`.
- `.mvn/jvm.config` carries the `--add-exports`/`--add-opens` flags these processors need — don't remove them.

## Architecture

- Entrypoint: `io.github.opendonationassistant.Application`. It sets the default environment to `standalone` via `@ContextConfigurer` (so `application-standalone.yml` supplies JDBC defaults) and declares RabbitMQ exchange/queue bindings in `rabbitConfiguration()`.
- **RabbitMQ**: two `@RabbitListener`s — `EventsListener` (queue `automation.events`) and `ProcessingListener` (queue `automation.processing`). Each exposes a static `BINDING` list of `Exchange`s that `Application.rabbitConfiguration()` aggregates. When adding an event type, update the relevant `BINDING` list.
- Commands are sent through the `@Named("commands")` `RabbitClient` bean (see `ActionFactory`).
- **Domain flow**: `ProcessingListener` → `IterationFactory.create(recipientId, source)` → `Iteration.run()` evaluates each rule's triggers and executes matched actions. `TriggerFactory`/`ActionFactory` map string IDs to domain objects via switch expressions.
- **Persistence**: Micronaut Data JDBC (`@JdbcRepository(dialect = Dialect.POSTGRES)`) + Flyway migrations in `src/main/resources/db/migration/` (V1–V7). All tables live in the `automation` schema.
- **REST**: controllers implement hand-written `*Api.java` interfaces annotated with Swagger/OpenAPI and `@Secured(IS_AUTHENTICATED)`. Auth is JWT bearer via Keycloak JWKS (`JWKS_URI` env var); `getOwnerId(auth)` reads the `preferred_username` claim and controllers return 401 when absent.
- **Platform libraries** (do not reimplement): `oda-rabbit-conf`, `oda-commons`, `oda-test-utils` (version `oda.version` = 0.11.232) provide `RabbitClient`, `UIFacade`, `MessageProcessor`, `BaseController`, `ODALogger`, and event/command records. Source lives in the `OpenDonationAssistant/oda-libraries` repo.

## Gotchas

- `TEST-REPORT.md` is a generated test-gap analysis (July 2026). Useful for finding missing tests, but **verify claims against current code** — e.g. it says `ChannelRaidedTrigger` matches `TwitchStreamStartedEvent`, but the code now correctly checks `TwitchChannelRaidEvent`.
- Known behaviors — don't "fix" without discussion:
  - `TwitchChannelSubscriptionMessageEventHandler` emits event type `"TwitchChannelSubscribeEvent"` (same as the subscribe handler).
  - `RunReelAction` sends `TriggerReelCommand` with all-empty-string fields (placeholder).
  - `TwitchChannelSubscribeEventHandler` silently skips gift subs (`// TODO use config`).
- Release versioning is CI-driven: push to `master` triggers `OpenDonationAssistant/oda-libraries/.github/workflows/release_service.yml@master` with version = `github.RUN_NUMBER`. Don't manually bump the `pom.xml` version for releases.
- `Dockerfile` expects a native executable at `target/oda-automation-service` (build with `-Dpackaging=native-image`); plain `./mvnw package` produces a jar.
- AOT is disabled (`micronaut.aot.enabled=false`); `aot-jar.properties` is not active.