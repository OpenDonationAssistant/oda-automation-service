# PROJECT KNOWLEDGE BASE

**Generated:** 2026-09-12
**Commit:** 770c143
**Branch:** master

## OVERVIEW
Java 25 / Micronaut 5.1.0 backend service in the OpenDonationAssistant platform. Event-driven: consumes RabbitMQ events, evaluates automation rules (triggers → actions), persists to PostgreSQL, and exposes REST APIs for automation state and alerts.

## STRUCTURE
```
oda-automation-service/
├── src/main/java/io/github/opendonationassistant/
│   ├── Application.java          # entry point + @Factory (Rabbit bindings)
│   ├── EventsListener.java       # Rabbit consumer → automation.events
│   ├── ProcessingListener.java   # Rabbit consumer → automation.processing
│   ├── automation/               # CORE: rule engine (66 files) — see its AGENTS.md
│   ├── alert/                    # alerts feature (api, repository, view)
│   ├── scheduledrun/             # scheduled-run feature
│   └── wordblacklist/            # word-filter feature (api, commands, view)
├── src/main/resources/
│   ├── application*.yml          # base + standalone + allinone env config
│   ├── logback.xml               # structured JSON logging
│   └── db/migration/             # Flyway V1–V8 (schema `automation`)
├── src/test/java/                # 17 test classes, mirrors main packages
├── pom.xml                       # Micronaut parent 5.1.0, JDK 25
├── Dockerfile                    # runtime-only: copies native binary
└── .github/workflows/maven.yml   # CI → shared oda-libraries release flow
```

## WHERE TO LOOK
| Task | Location | Notes |
|------|----------|-------|
| Rule engine (triggers/actions) | `automation/domain/` | Iteration, factories, actions, triggers |
| RabbitMQ event handlers | `automation/listener/messagehandlers/` | alert/recipient/twitch sub-packages |
| REST controllers | `automation/{commands,view}/`, `alert/view/`, `wordblacklist/` | implement `*Api.java` interfaces |
| Persistence | `automation/repository/`, `alert/repository/`, `scheduledrun/repository/` | Micronaut Data JDBC |
| DB schema | `src/main/resources/db/migration/` | Flyway V1–V8 |
| Tests | `src/test/java/...` | mirrors main packages |
| Test gap analysis | `TEST-REPORT.md` | July 2026, verify claims against code |

## CODE MAP
Reference centrality unmeasured (no LSP/ast-grep available). Key symbols:

| Symbol | Type | Location | Role |
|--------|------|----------|------|
| `Application` | class | root pkg | entry point, Rabbit bindings, `@Named("commands")` client |
| `EventsListener` | @RabbitListener | root pkg | consumes `automation.events` |
| `ProcessingListener` | @RabbitListener | root pkg | consumes `automation.processing`, runs iterations |
| `Iteration` / `IterationFactory` | class | automation/domain | rule evaluation engine |
| `TriggerFactory` / `ActionFactory` | class | automation/domain/{trigger,action} | string ID → domain object via switch |
| `AutomationController` | @Controller | automation/view | `/automation/...` REST |
| `GoalListener` | @RabbitListener | automation/listener | reacts to `UpdatedGoal` |
| `AutomationMetrics` | class | automation/metrics | Prometheus counters |

## CONVENTIONS
- **NullAway enforced as ERROR** (JSpecify mode): annotate with `@Nullable`/`@NonNull` from `org.jspecify.annotations`. Generated sources excluded.
- Error Prone with `-XDcompilePolicy=simple`, `--should-stop=ifError=FLOW`. `.mvn/jvm.config` carries required `--add-exports`/`--add-opens` — don't remove.
- Java records for DTOs and `@MappedEntity` models, annotated `@Serdeable`.
- 2-space indentation; `Optional` + streams; `@Singleton` + constructor `@Inject`.
- Layered packaging: `api/` (OpenAPI interfaces), `commands/`+`view/` (controllers), `dto/`, `repository/`, `domain/`.
- Env-var config: `JDBC_URL`, `JDBC_USER`, `JDBC_PASSWORD`, `JWKS_URI`.
- Tests: JUnit 5 + Mockito + Instancio + Hamcrest; DB tests via Testcontainers PostgreSQL 14.

## ANTI-PATTERNS (THIS PROJECT)
- Do NOT reimplement platform libraries: `oda-rabbit-conf`, `oda-commons`, `oda-test-utils` (oda.version 0.11.232) provide `RabbitClient`, `UIFacade`, `MessageProcessor`, `BaseController`, `ODALogger`, event/command records.
- Do NOT manually bump `pom.xml` version for releases — CI tags with `github.RUN_NUMBER`.
- Do NOT remove `.mvn/jvm.config` flags — annotation processors fail without them.
- Do NOT "fix" known behaviors without discussion (see NOTES).

## UNIQUE STYLES
- `@ContextConfigurer` nested in `Application` forces `standalone` environment at startup.
- Static `BINDING` lists on listeners; `Application.rabbitConfiguration()` aggregates them. When adding an event type, update the relevant `BINDING`.
- Controllers read owner via `getOwnerId(auth)` from `preferred_username` claim; 401 when absent.
- Dynamic packaging: `<packaging>${packaging}</packaging>` switches jar ↔ native-image via `-Dpackaging=native-image`.

## COMMANDS
```bash
./mvnw package          # build (jar)
./mvnw test             # test — needs Testcontainers via podman (see NOTES)
./mvnw test -Dtest=ClassName   # single test
./mvnw clean package -Dpackaging=native-image -DskipTests   # native binary (CI)
```

## NOTES
- Tests use Testcontainers (PostgreSQL 14) via **podman, NOT Docker**. Env needs `DOCKER_HOST=unix:///run/user/1000/podman/podman.sock` and `TESTCONTAINERS_RYUK_DISABLED=true`.
- DB-backed tests use `@MicronautTest(environments = "allinone")`; test datasource `jdbc:tc:postgresql:14:///postgres`.
- `TEST-REPORT.md` is a generated test-gap analysis (July 2026) — verify claims against current code.
- Known behaviors — don't "fix" without discussion:
  - `TwitchChannelSubscriptionMessageEventHandler` emits `"TwitchChannelSubscribeEvent"` (same as subscribe handler).
  - `RunReelAction` sends `TriggerReelCommand` with all-empty-string fields (placeholder).
  - `TwitchChannelSubscribeEventHandler` silently skips gift subs (`// TODO use config`).
- `Dockerfile` expects native executable at `target/oda-automation-service`; plain `./mvnw package` produces a jar.
- AOT disabled (`micronaut.aot.enabled=false`); `aot-jar.properties` is inactive.
- `Dockerfile` LABEL has typo `opendonationasssistant` (extra s).
- `.tmp/` scratch dir is excluded only by user-global gitignore — don't commit it.