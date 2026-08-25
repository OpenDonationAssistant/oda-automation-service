# Test Coverage Analysis — Missing Test Cases

> Generated: July 2026
> Project: oda-automation-service

---

## Existing Test Coverage Summary

**7 test files, 18 active tests, 1 disabled test.**

| Test File | What It Tests | Active Tests |
|---|---|---|
| `AlertRepositoryTest` | `AlertRepository.create()` with/without link | 2 |
| `IterationTest` | `Iteration.run()` with trigger / missing trigger | 2 (+1 @Disabled) |
| `TwitchChannelCheerEventHandlerTest` | Cheer event → UI event w/ and w/o username | 2 |
| `DeletedHistoryItemHandlerTest` | Hide alerts on delete by originId | 3 |
| `SetStateTest` | Creating/updating automation state | 2 |
| `AutomationRuleRepositoryTest` | Create + read rule by recipientId+ruleId | 1 |
| `AutomationVariableRepositoryTest` | Full variable CRUD (create/update/delete) | 6 |

---

## Missing Tests by Priority

### 🔴 HIGH PRIORITY — Core Business Logic (40+ test scenarios)

These are the most important gaps — domain logic with real business rules, conditional branches, and side effects.

#### 1. `FilledDonationGoalTrigger` — Goal matching + amount comparison logic
- [ ] Trigger fires when Goal widgetId matches AND accumulatedAmount >= requiredAmount
- [ ] Trigger does not fire when widgetId does not match
- [ ] Trigger does not fire when accumulatedAmount < requiredAmount
- [ ] Trigger does not fire when target is not a `Goal` instance
- [ ] Trigger does not fire when `widgetId` is not configured (empty Optional)
- [ ] Boundary: accumulatedAmount == requiredAmount (should fire)
- [ ] Boundary: accumulatedAmount just below requiredAmount (should not fire)
- [ ] `getWidgetId()` returns correct Optional (present / empty)

#### 2. `IncreaseVariableAction` — Numeric variable increment logic
- [ ] Increments variable value by configured amount
- [ ] Does nothing when variableId not found in DB (silent Optional drop)
- [ ] Does nothing when found variable is not `AutomationNumberVariable` (wrong type)
- [ ] Defaults to +0 when `amount` not in action data
- [ ] `getVariableId()` and `getAmount()` return correct values

#### 3. `IncreaseDonationGoalAction` — Goal required-amount modification
- [ ] Increases `requiredAmount` by configured amount when source is a Goal
- [ ] Defaults to +0 increase when `amount` not configured
- [ ] Does nothing when source is not a Goal (null-safe)
- [ ] `getIncreaseAmount()`, `getWidgetId()`, `getGoalId()` return correct values

#### 4. `RefreshDonationGoalAction` — Goal refresh calculation
- [ ] Sets `accumulatedAmount` to `(accumulated - required)` diff when accumulated > required
- [ ] Sets `accumulatedAmount` to 0 when accumulated < required (floor at 0)
- [ ] Boundary: accumulated == required → diff = 0
- [ ] Does nothing when source is not a Goal

#### 5. `PinTwitchMessageAction` — RabbitMQ command with null guards
- [ ] Sends `SendAndPinChatMessageCommand` when all 3 fields present
- [ ] Returns early when `recipientId` is null
- [ ] Returns early when `refreshTokenId` is null
- [ ] Returns early when `message` is null

#### 6. `TwitchShoutoutAction` — Shoutout command via iteration variable
- [ ] Sends `TwitchShoutoutCommand` when `fromTwitchId` variable is present
- [ ] Returns early when `fromTwitchId` variable is absent
- [ ] Correct `recipientId` and `targetTwitchId` in command

#### 7. `RunReelAction` — Reel trigger command
- [ ] Sends `TriggerReelCommand` when `reelId` is present
- [ ] Returns early when `reelId` is null
- [ ] ⚠️ *Note: command fields are all empty strings — potential placeholder/bug*

#### 8. `TriggerFactory` — Trigger ID → instance mapping
- [ ] `"donationgoal-filled"` → `FilledDonationGoalTrigger`
- [ ] `"stream-started"` → `StreamStartedTrigger`
- [ ] `"channel-raided"` → `StreamStartedTrigger` *(same as above — test documents this)*
- [ ] Unknown ID → `NeverTrigger`
- [ ] Both `create()` and `from()` factory methods tested

#### 9. `ActionFactory` — Action ID → instance mapping
- [ ] `"increase-donation-goal"` → `IncreaseDonationGoalAction`
- [ ] `"refresh-donation-goal"` → `RefreshDonationGoalAction`
- [ ] `"increase-variable"` → `IncreaseVariableAction`
- [ ] `"run-reel"` → `RunReelAction`
- [ ] `"pin-twitch-message"` → `PinTwitchMessageAction`
- [ ] `"twitch-shoutout"` → `TwitchShoutoutAction`
- [ ] Unknown ID → `EmptyAutomationAction`

#### 10. `StreamStartedTrigger` — Simple trigger matching
- [ ] Returns `true` for `TwitchStreamStartedEvent`
- [ ] Returns `false` for other event types
- [ ] `extractVariables` is a no-op

#### 11. `ChannelRaidedTrigger` — Trigger matching (possible bug)
- [ ] **Anomaly**: `isTriggered()` checks `target instanceof TwitchStreamStartedEvent`, not `TwitchChannelRaidEvent`
- [ ] Returns true for stream-started events
- [ ] Returns false for actual raid events
- [ ] `extractVariables` casts to `TwitchChannelRaidEvent` (but does nothing)

#### 12. `AutomationNumberVariable` — BigDecimal parsing + defaults
- [ ] Returns `BigDecimal.ZERO` when stored value is empty string
- [ ] Parses valid BigDecimal strings correctly
- [ ] `setValue()` calls `update()` with correct `AutomationVariableData`
- [ ] Edge cases: negative numbers, very large decimals

#### 13. `AutomationVariable` — Generic variable CRUD (unit test with mocks)
- [ ] `update(name, value)` creates new `AutomationVariableData` with `value.toString()`
- [ ] `save()` delegates to `repository.update()`
- [ ] `delete()` delegates to `repository.deleteById()`

#### 14. `AutomationRule` — Rule CRUD + factory delegation
- [ ] `save()` calls `repository.update()`
- [ ] `delete()` calls `repository.deleteById()`
- [ ] `getTriggers()` calls `triggerFactory.from()` on each `AutomationTriggerData`
- [ ] `getActions()` calls `actionFactory.from()` on each `AutomationActionData`
- [ ] `update()` replaces data and saves

---

### 🟡 MEDIUM PRIORITY — Event Handlers + Integration (30+ test scenarios)

#### 15. `CreateAlertCommandHandler` — Alert creation + URL rewriting
- [ ] Creates `AlertData` with all fields from command
- [ ] URL conversion: `files.donationalerts.com` → `widgets.oda.digital`
- [ ] URL conversion: `https://cdn.donatex.gg/donation-voices` → `https://widgets.oda.digital/external/donatex`
- [ ] Unmatched URLs passed through unchanged
- [ ] Null URL → no media object created
- [ ] Calls `repository.create()` with correct system/event/paymentId
- [ ] Null `count` and `levelName` are handled

#### 16. `PaymentEventHandler` — Payment → alert mapping
- [ ] Creates `AlertData` from `PaymentEvent` using `cleanNickname()` / `cleanMessage()`
- [ ] Calls `repository.create("ODA", "payment", message.id(), data)`
- [ ] Null cleanNickname/cleanMessage are persisted

#### 17. `GoalListener` — Iterative stability loop
- [ ] Loops until no change in accumulated or required amounts
- [ ] Detects accumulatedAmount change
- [ ] Detects requiredAmount change
- [ ] No change → exits loop after single iteration
- [ ] Sends `UpdatedGoal` via `UpdatedGoalSender` after processing
- [ ] Creates `Goal` and passes through `IterationFactory`

#### 18. `RepeatAlertCommandHandler` — Alert re-send logic
- [ ] Sends forced alert (`alert.send(true)`) by `alertId`
- [ ] Sends forced alert by `originId` (multiple links possible)
- [ ] Does nothing when both `alertId` and `originId` are null

#### 19. `HistoryItemEventHandler` — Send non-forced alerts
- [ ] Sends non-forced alert (`alert.send(false)`) by `originId`
- [ ] Does nothing when `originId` is null
- [ ] Multiple links are all sent

#### 20. `IterationFactory` — Iteration construction
- [ ] Creates `Iteration` with correct `recipientId`, `source`
- [ ] Loads variables from `variableRepository.listByRecipientId()`
- [ ] Loads rules from `ruleRepository.listByRecipientId()`

#### 21. `TwitchUserBannedEventHandler` — UI event construction
- [ ] Creates event with correct id, type (`"TwitchUserBannedEvent"`), `nickname` variable

#### 22. `TwitchChannelRaidEventHandler` — UI event construction
- [ ] Creates event with correct type, `channel` (string) and `viewerCount` (number) variables

#### 23. `TwitchChannelSubscribeEventHandler` — UI event + gift sub skip
- [ ] Creates event with `nickname`, `tier`, `isGift` variables
- [ ] **Skips gift subs** (returns early when `isGift == true`)

#### 24. `TwitchChannelSubscriptionGiftEventHandler` — UI event construction
- [ ] Creates event with `nickname`, `amount`, `tier` variables

#### 25. `TwitchChannelFollowEventHandler` — UI event construction
- [ ] Creates event with `nickname` variable

#### 26. `TwitchChannelSubscriptionMessageEventHandler` — UI event construction
- [ ] Creates event with `nickname`, `tier`, `message`, `cumulativeMonths`, `totalMonths`, `streakMonths`

#### 27. `GoalHistoryEventHandler` — UI event construction
- [ ] Creates `"GoalUpdate"` event with `widgetId`, `goalId`, `newAccumulatedAmount`

#### 28. `MediaHistoryEventHandler` — UI event construction
- [ ] Creates `"MediaRequested"` event with `url`, `title`, `thumbnail`, `mediaId`, `source`, `originId`

#### 29. `ReelResultHistoryEventHandler` — UI event construction
- [ ] Creates `"ReelResult"` event with `widgetId`, `optionId`, `title`

#### 30. `WidgetChangedEventHandler` — UI reload
- [ ] Calls `ui.reload(ownerId, widgetId)` with correct values

#### 31. `AlertController` — Pagination + time filtering
- [ ] `listAlerts()` returns paginated results
- [ ] Time-based filtering with after/before parameters
- [ ] Owner extracted from authentication

#### 32. `AutomationController` — State queries
- [ ] `listVariables()` returns variables for authenticated user
- [ ] `listAutomations()` returns rules for authenticated user
- [ ] `getState()` returns combined `AutomationDto`

---

### 🟢 LOWER PRIORITY — Service / Infrastructure

#### 33. `ProcessingListener` — Event conversion + iteration
- [ ] `TwitchStreamStartedEvent` converts and processes correctly
- [ ] `TwitchChannelRaidEvent` converts and processes correctly
- [ ] Unknown type → `Optional.empty()` and no processing
- [ ] Exception handling logs but does not re-throw

#### 34. `Alert.send()` — Complex notification construction (138 lines)
- [ ] Sends `AlertNotification` with correct fields from data/link
- [ ] Sends UI Event with all variables: `force`, `system`, `originId`, `event`, `nickname`, `amount`, `alertmedia`, `message`, `levelName`, `count`
- [ ] Null-safe handling of optional fields (no NPEs)
- [ ] `force` parameter propagated to notification variables

---

### Gaps in Existing Tests

#### 35. `IterationTest` — Missing scenarios
- [ ] Multiple rules where only one triggers
- [ ] `iteration.add()` adds a variable correctly
- [ ] `iteration.variable()` lookup works
- [ ] Empty rules list
- [ ] **Review/reenable `@Disabled testConflictingTriggers`**
- [ ] Variable extraction from trigger propagates to iteration

#### 36. `AlertRepositoryTest` — Missing scenarios
- [ ] `get()` method (find by ID)
- [ ] Alert not found returns `Optional.empty()`

#### 37. `AutomationVariableRepositoryTest` — Missing scenarios
- [ ] `listByRecipientId()` returns all variables for a recipient
- [ ] `update()` service method
- [ ] `convert()` handles unknown type gracefully
- [ ] RecipientId with no variables returns empty list

#### 38. `AutomationRuleRepositoryTest` — Missing scenarios
- [ ] `listByRecipientId()` returns all rules for a recipient
- [ ] Rule not found returns `Optional.empty()`
- [ ] RecipientId with no rules returns empty

#### 39. `SetStateTest` — Missing scenarios
- [ ] Empty rules / variables lists
- [ ] Authentication missing `preferred_username` attribute
- [ ] Partial state update (only rules, no variables, or vice versa)
- [ ] Variable with null type

---

### ⚠️ Anomalies That Tests Would Document

1. **`ChannelRaidedTrigger.isTriggered()`** checks for `TwitchStreamStartedEvent`, not `TwitchChannelRaidEvent` — name says "raided", but implementation triggers on stream start
2. **`TwitchChannelSubscriptionMessageEventHandler`** names its event `"TwitchChannelSubscribeEvent"` (same as the subscribe handler) instead of `"TwitchChannelSubscriptionMessageEvent"`
3. **`RunReelAction`** sends `TriggerReelCommand` with all empty string fields — looks like incomplete implementation
4. **`TwitchChannelSubscribeEventHandler`** silently skips gift subs with a `// TODO use config` comment — this behavior change would be caught by tests

---

### Summary

| Priority | Additional Test Files Needed | Missing Test Scenarios |
|---|---|---|
| 🔴 High | ~7 new test files | 40+ |
| 🟡 Medium | ~4 new test files (+ filling gaps) | 30+ |
| 🟢 Low | ~2 new test files | 10+ |
| **Total** | **~13 new test files** | **~80+** |

**Biggest gaps**: (1) all 6 action implementations have zero tests, (2) 12 of 14 event handlers have no tests, (3) TriggerFactory edge cases and the `ChannelRaidedTrigger` anomaly, (4) the `IterationTest` disabled test and missing scenarios, (5) no integration test for `GoalListener` stability loop or `ProcessingListener` event conversion.
