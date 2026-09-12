# automation — Rule Engine Core

**Generated:** 2026-09-12

## OVERVIEW
Core domain of oda-automation-service: evaluates automation rules (triggers → actions) from RabbitMQ events, persists rules/variables, exposes REST for automation state. 66 files / 3344 lines.

## STRUCTURE
```
automation/
├── api/          # OpenAPI interfaces (AutomationOperationsApi, SetStateApi, ...)
├── commands/     # @Controllers: TriggerRule, SetState, SetEnabled
├── domain/       # engine: Iteration, IterationFactory
│   ├── action/   # 8 actions (IncreaseVariable, RunReel, TwitchAnnounce, ...)
│   ├── trigger/  # 5 triggers (Command, StreamStarted, ChannelRaided, ...)
│   ├── goal/     # Goal model
│   └── variable/ # AutomationStringVariable / AutomationNumberVariable
├── dto/          # AutomationRuleDto, AutomationVariableDto, ...
├── listener/     # GoalListener + messagehandlers/{alert,recipient,twitch}
├── metrics/      # AutomationMetrics (Prometheus counters)
├── repository/   # rule + variable repositories (Micronaut Data JDBC)
└── view/         # AutomationController (/automation/...)
```

## WHERE TO LOOK
| Task | Location |
|------|----------|
| Add a trigger | `domain/trigger/` + `TriggerFactory` switch |
| Add an action | `domain/action/` + `ActionFactory` switch |
| Handle a new RabbitMQ event | `listener/messagehandlers/<category>/` + update owning listener's `BINDING` |
| Rule CRUD / state | `repository/`, `commands/`, `view/` |
| Metrics | `metrics/AutomationMetrics` |

## CONVENTIONS
- `TriggerFactory`/`ActionFactory` map string IDs via switch expressions; unknown IDs → `NeverTrigger`/no-op.
- Actions send commands via `@Named("commands")` `RabbitClient` bean.
- Message handlers extend `AbstractMessageHandler` (from oda-commons), auto-registered as `@Singleton`.
- Repositories: `@JdbcRepository(dialect = Dialect.POSTGRES)`, tables in `automation` schema.
- DTOs and `@MappedEntity` models are Java records annotated `@Serdeable`.

## ANTI-PATTERNS
- Don't add event types without updating the owning listener's static `BINDING` list.
- Don't reimplement `MessageProcessor`/`UIFacade` — use oda-commons.
- Don't break NullAway: annotate nullability on new domain/DTO fields.