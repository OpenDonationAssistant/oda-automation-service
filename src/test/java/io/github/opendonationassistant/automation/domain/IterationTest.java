package io.github.opendonationassistant.automation.domain;

import static org.mockito.Mockito.verify;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.domain.action.ActionFactory;
import io.github.opendonationassistant.automation.domain.action.PinTwitchMessageAction.SendAndPinChatMessageCommand;
import io.github.opendonationassistant.automation.domain.trigger.TriggerFactory;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationRuleData;
import io.github.opendonationassistant.automation.repository.AutomationRuleDataRepository;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.events.twitch.events.TwitchStreamStartedEvent;
import io.github.opendonationassistant.rabbit.RabbitClient;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class IterationTest {

  AutomationRuleDataRepository rules = Mockito.mock(
    AutomationRuleDataRepository.class
  );
  AutomationVariableRepository variables = Mockito.mock(
    AutomationVariableRepository.class
  );
  RabbitClient rabbit = Mockito.mock(RabbitClient.class);
  TriggerFactory triggers = Mockito.spy(new TriggerFactory());
  ActionFactory actions = Mockito.spy(new ActionFactory(variables, rabbit));

  @Test
  public void testRunningActionIfTriggerFired() {
    var triggerData = new AutomationTriggerData("stream-started", Map.of());
    var actionData = new AutomationActionData(
      "pin-twitch-message",
      Map.of(
        "recipientId",
        "testuser",
        "refreshTokenId",
        "refreshTokenId",
        "message",
        "message"
      )
    );
    AutomationRuleData data = new AutomationRuleData(
      "id",
      "name",
      "testuser",
      List.of(triggerData),
      List.of(actionData)
    );
    AutomationRule rule = new AutomationRule(rules, triggers, actions, data);

    new Iteration(
      "testuser",
      new TwitchStreamStartedEvent("eventId", "testuser", "url"),
      List.of(),
      List.of(rule)
    ).run();
    verify(triggers).from(triggerData);
    verify(actions).from("testuser", actionData);
    verify(rabbit).sendCommand(
      new SendAndPinChatMessageCommand("testuser", "refreshTokenId", "message")
    );
  }

  @Test
  public void testNoTriggers() {
    var actionData = new AutomationActionData(
      "pin-twitch-message",
      Map.of(
        "recipientId",
        "testuser",
        "refreshTokenId",
        "refreshTokenId",
        "message",
        "message"
      )
    );
    AutomationRuleData data = new AutomationRuleData(
      "id",
      "name",
      "testuser",
      List.of(),
      List.of(actionData)
    );
    AutomationRule rule = new AutomationRule(rules, triggers, actions, data);

    new Iteration(
      "testuser",
      new TwitchStreamStartedEvent("eventId", "testuser", "url"),
      List.of(),
      List.of(rule)
    ).run();

    Mockito.verifyNoInteractions(rabbit);
  }

  @Test
  @Disabled
  public void testConflictingTriggers() {
    var firstTriggerData = new AutomationTriggerData(
      "stream-started",
      Map.of()
    );
    var secondTriggerData = new AutomationTriggerData(
      "channel-raided",
      Map.of()
    );
    var actionData = new AutomationActionData(
      "pin-twitch-message",
      Map.of(
        "recipientId",
        "testuser",
        "refreshTokenId",
        "refreshTokenId",
        "message",
        "message"
      )
    );
    AutomationRuleData data = new AutomationRuleData(
      "id",
      "name",
      "testuser",
      List.of(firstTriggerData, secondTriggerData),
      List.of(actionData)
    );
    AutomationRule rule = new AutomationRule(rules, triggers, actions, data);

    new Iteration(
      "testuser",
      new TwitchStreamStartedEvent("eventId", "testuser", "url"),
      List.of(),
      List.of(rule)
    ).run();
    Mockito.verifyNoInteractions(rabbit);
  }
}
