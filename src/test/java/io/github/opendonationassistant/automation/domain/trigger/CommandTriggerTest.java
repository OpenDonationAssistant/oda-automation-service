package io.github.opendonationassistant.automation.domain.trigger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.api.TriggerRuleApi.TriggerRuleCommand;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.action.ActionFactory;
import io.github.opendonationassistant.automation.domain.action.PinTwitchMessageAction.SendAndPinChatMessageCommand;
import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationRuleData;
import io.github.opendonationassistant.automation.repository.AutomationRuleDataRepository;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CommandTriggerTest {

  AutomationRuleDataRepository rules = Mockito.mock(
    AutomationRuleDataRepository.class
  );
  AutomationVariableRepository variables = Mockito.mock(
    AutomationVariableRepository.class
  );
  RabbitClient rabbit = Mockito.mock(RabbitClient.class);
  TriggerFactory triggers = Mockito.spy(new TriggerFactory());
  ActionFactory actions = Mockito.spy(new ActionFactory(variables, rabbit));
  AutomationMetrics metrics = new AutomationMetrics(new SimpleMeterRegistry());

  @Test
  public void testTriggeredByCommandWithMatchingRuleId() {
    var trigger = new CommandTrigger(
      new AutomationTriggerData("command", Map.of("ruleId", "rule-id"))
    );
    assertTrue(
      trigger.isTriggered(
        new TriggerRuleCommand("rule-id", "nickname", "system", Map.of())
      )
    );
  }

  @Test
  public void testNotTriggeredByCommandWithOtherRuleId() {
    var trigger = new CommandTrigger(
      new AutomationTriggerData("command", Map.of("ruleId", "rule-id"))
    );
    assertFalse(
      trigger.isTriggered(
        new TriggerRuleCommand("other-rule-id", "nickname", "system", Map.of())
      )
    );
  }

  @Test
  public void testNotTriggeredByCommandWithoutRuleId() {
    var trigger = new CommandTrigger(
      new AutomationTriggerData("command", Map.of())
    );
    assertFalse(
      trigger.isTriggered(
        new TriggerRuleCommand("rule-id", "nickname", "system", Map.of())
      )
    );
  }

  @Test
  public void testNotTriggeredByOtherSource() {
    var trigger = new CommandTrigger(
      new AutomationTriggerData("command", Map.of("ruleId", "rule-id"))
    );
    assertFalse(trigger.isTriggered(new Object()));
  }

  @Test
  public void testCommandTriggerFiresForMatchingRuleId() {
    var triggerData = new AutomationTriggerData(
      "command",
      Map.of("ruleId", "rule-id")
    );
    var actionData = new AutomationActionData(
      "pin-twitch-message",
      Map.of(
        "recipientId",
        "testuser",
        "senderRefreshTokenId",
        "senderRefreshTokenId",
        "recipientTwitchId",
        "recipientTwitchId",
        "message",
        "message"
      )
    );
    AutomationRuleData data = new AutomationRuleData(
      "rule-id",
      "name",
      "testuser",
      List.of(triggerData),
      List.of(actionData),
      true
    );
    AutomationRule rule = new AutomationRule(rules, triggers, actions, data);

    new Iteration(
      "testuser",
      new TriggerRuleCommand(
        "rule-id",
        "nickname",
        "system",
        Map.of("var", "value")
      ),
      List.of(),
      List.of(rule),
      metrics
    ).run();
    verify(rabbit).sendCommand(
      new SendAndPinChatMessageCommand(
        "testuser",
        "senderRefreshTokenId",
        "recipientTwitchId",
        "message"
      )
    );
  }

  @Test
  public void testCommandTriggerDoesNotFireForOtherRuleId() {
    var triggerData = new AutomationTriggerData(
      "command",
      Map.of("ruleId", "rule-id")
    );
    var actionData = new AutomationActionData(
      "pin-twitch-message",
      Map.of(
        "recipientId",
        "testuser",
        "senderRefreshTokenId",
        "senderRefreshTokenId",
        "recipientTwitchId",
        "recipientTwitchId",
        "message",
        "message"
      )
    );
    AutomationRuleData data = new AutomationRuleData(
      "rule-id",
      "name",
      "testuser",
      List.of(triggerData),
      List.of(actionData),
      true
    );
    AutomationRule rule = new AutomationRule(rules, triggers, actions, data);

    new Iteration(
      "testuser",
      new TriggerRuleCommand("other-rule-id", "nickname", "system", Map.of()),
      List.of(),
      List.of(rule),
      metrics
    ).run();
    Mockito.verifyNoInteractions(rabbit);
  }

  @Test
  public void testExtractingVariables() {
    var trigger = new CommandTrigger(
      new AutomationTriggerData("command", Map.of("ruleId", "rule-id"))
    );
    var iteration = new Iteration(
      "testuser",
      new TriggerRuleCommand("rule-id", "nickname", "system", Map.of("var1", "val1")),
      List.of(),
      List.of(),
      metrics
    );
    trigger.extractVariables(
      new TriggerRuleCommand("rule-id", "nickname", "system", Map.of("var1", "val1")),
      iteration
    );
    assertEquals("nickname", iteration.variable("nickname").get().value());
    assertEquals("system", iteration.variable("system").get().value());
    assertEquals("val1", iteration.variable("var1").get().value());
  }

  @Test
  public void testExtractingVariablesWithoutOptionalFields() {
    var trigger = new CommandTrigger(
      new AutomationTriggerData("command", Map.of("ruleId", "rule-id"))
    );
    var iteration = new Iteration(
      "testuser",
      new TriggerRuleCommand("rule-id", null, null, null),
      List.of(),
      List.of(),
      metrics
    );
    trigger.extractVariables(
      new TriggerRuleCommand("rule-id", null, null, null),
      iteration
    );
    assertTrue(iteration.variable("nickname").isEmpty());
    assertTrue(iteration.variable("system").isEmpty());
  }
}

