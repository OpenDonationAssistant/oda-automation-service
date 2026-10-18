package io.github.opendonationassistant.automation.listener.messagehandlers.webhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.listener.messagehandlers.webhook.WebhookChangedEventHandler.Event;
import io.github.opendonationassistant.automation.listener.messagehandlers.webhook.WebhookChangedEventHandler.Type;
import io.github.opendonationassistant.automation.listener.messagehandlers.webhook.WebhookChangedEventHandler.WebhookChangedEvent;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationRuleData;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class WebhookChangedEventHandlerTest {

  private static final String RECIPIENT_ID = "recipient-1";
  private static final String RULE_ID = "webhook-1";
  private static final String NAME = "Webhook DISCORD";
  private static final List<AutomationTriggerData> TRIGGERS = List.of(
    new AutomationTriggerData("donation", Map.of())
  );
  private static final List<AutomationActionData> ACTIONS = List.of(
    new AutomationActionData(
      "send-webhook-message",
      Map.of(
        "message",
        "<nickname> задонатил <amount> со словами: <message>"
      )
    )
  );

  private final AutomationRuleRepository rules = mock(
    AutomationRuleRepository.class
  );
  private final WebhookChangedEventHandler handler =
    new WebhookChangedEventHandler(mock(ObjectMapper.class), rules);

  private WebhookChangedEvent event(Event event, boolean enabled) {
    return new WebhookChangedEvent(
      RULE_ID,
      RECIPIENT_ID,
      "https://discord.com/api/webhooks/1",
      Type.DISCORD,
      Map.of(),
      enabled,
      event
    );
  }

  @Test
  public void testType() {
    // Assert the Rabbit binding type is derived from the payload class name.
    assertEquals("WebhookChangedEvent", handler.type());
  }

  @Test
  public void testWebhookCreatedCreatesAutomationRule() throws IOException {
    // Arrange
    var message = event(Event.WEBHOOK_CREATED, true);

    // Act
    handler.handle(message);

    // Assert
    verify(rules)
      .create(RECIPIENT_ID, RULE_ID, NAME, TRIGGERS, ACTIONS, true);
  }

  @Test
  public void testWebhookUpdatedUpdatesResolvedRule() throws IOException {
    // Arrange
    var rule = mock(AutomationRule.class);
    when(rules.getByRecipientIdAndRuleId(RECIPIENT_ID, RULE_ID)).thenReturn(
      Optional.of(rule)
    );
    var message = event(Event.WEBHOOK_UPDATED, true);

    // Act
    handler.handle(message);

    // Assert
    ArgumentCaptor<AutomationRuleData> captor = ArgumentCaptor.forClass(
      AutomationRuleData.class
    );
    verify(rule).update(captor.capture());

    var data = captor.getValue();
    assertEquals(RULE_ID, data.id());
    assertEquals(NAME, data.name());
    assertEquals(RECIPIENT_ID, data.recipientId());
    assertEquals(TRIGGERS, data.triggers());
    assertEquals(ACTIONS, data.actions());
    assertTrue(data.enabled());
  }

  @Test
  public void testWebhookDeletedDeletesResolvedRule() throws IOException {
    // Arrange
    var rule = mock(AutomationRule.class);
    when(rules.getByRecipientIdAndRuleId(RECIPIENT_ID, RULE_ID)).thenReturn(
      Optional.of(rule)
    );
    var message = event(Event.WEBHOOK_DELETED, true);

    // Act
    handler.handle(message);

    // Assert
    verify(rule).delete();
  }

  @Test
  public void testWebhookUpdatedWithMissingRuleDoesNotCreateOrDelete()
    throws IOException {
    // Arrange
    when(rules.getByRecipientIdAndRuleId(RECIPIENT_ID, RULE_ID)).thenReturn(
      Optional.empty()
    );
    var message = event(Event.WEBHOOK_UPDATED, true);

    // Act
    handler.handle(message);

    // Assert — only the lookup happens; no rule is updated/created/deleted.
    verify(rules).getByRecipientIdAndRuleId(RECIPIENT_ID, RULE_ID);
    verifyNoMoreInteractions(rules);
  }

  @Test
  public void testWebhookDeletedWithMissingRuleDoesNotDelete()
    throws IOException {
    // Arrange
    when(rules.getByRecipientIdAndRuleId(RECIPIENT_ID, RULE_ID)).thenReturn(
      Optional.empty()
    );
    var message = event(Event.WEBHOOK_DELETED, true);

    // Act
    handler.handle(message);

    // Assert — only the lookup happens; no delete is attempted.
    verify(rules).getByRecipientIdAndRuleId(RECIPIENT_ID, RULE_ID);
    verifyNoMoreInteractions(rules);
  }

  @Test
  public void testWebhookCreatedPropagatesDisabledFlag() throws IOException {
    // Arrange
    var message = event(Event.WEBHOOK_CREATED, false);

    // Act
    handler.handle(message);

    // Assert — the event's enabled flag is passed through unchanged.
    verify(rules)
      .create(RECIPIENT_ID, RULE_ID, NAME, TRIGGERS, ACTIONS, false);
  }
}
