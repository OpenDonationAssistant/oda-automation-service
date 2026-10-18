package io.github.opendonationassistant.automation.listener.messagehandlers.webhook;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationRuleData;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.HasRecipientId;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Singleton
public class WebhookChangedEventHandler
  extends AbstractMessageHandler<
    WebhookChangedEventHandler.WebhookChangedEvent
  > {

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

  private final ODALogger log = new ODALogger(this);
  private final AutomationRuleRepository rules;

  @Inject
  public WebhookChangedEventHandler(
    ObjectMapper mapper,
    AutomationRuleRepository rules
  ) {
    super(mapper);
    this.rules = rules;
  }

  @Serdeable
  public static record WebhookChangedEvent(
    String id,
    String recipientId,
    String url,
    Type type,
    Map<String, Object> settings,
    boolean enabled,
    Event event
  )
    implements HasRecipientId {}

  @Serdeable
  public static enum Event {
    WEBHOOK_CREATED,
    WEBHOOK_UPDATED,
    WEBHOOK_DELETED,
  }

  @Serdeable
  public static enum Type {
    DISCORD,
    TELEGRAM,
  }

  @Override
  public void handle(WebhookChangedEvent message) throws IOException {
    final String name = "Webhook " + message.type().name();
    switch (message.event()) {
      case WEBHOOK_CREATED -> {
        log.info(
          "Creating webhook automation rule",
          Map.of(
            "recipientId",
            message.recipientId(),
            "ruleId",
            message.id()
          )
        );
        rules.create(
          message.recipientId(),
          message.id(),
          name,
          TRIGGERS,
          ACTIONS,
          message.enabled()
        );
      }
      case WEBHOOK_UPDATED -> {
        log.info(
          "Updating webhook automation rule",
          Map.of(
            "recipientId",
            message.recipientId(),
            "ruleId",
            message.id()
          )
        );
        rules
          .getByRecipientIdAndRuleId(message.recipientId(), message.id())
          .ifPresent(rule ->
            rule.update(
              new AutomationRuleData(
                message.id(),
                name,
                message.recipientId(),
                TRIGGERS,
                ACTIONS,
                message.enabled()
              )
            )
          );
      }
      case WEBHOOK_DELETED -> {
        log.info(
          "Deleting webhook automation rule",
          Map.of(
            "recipientId",
            message.recipientId(),
            "ruleId",
            message.id()
          )
        );
        rules
          .getByRecipientIdAndRuleId(message.recipientId(), message.id())
          .ifPresent(AutomationRule::delete);
      }
    }
  }
}
