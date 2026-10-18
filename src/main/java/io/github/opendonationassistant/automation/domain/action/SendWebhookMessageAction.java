package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

public class SendWebhookMessageAction extends AutomationAction {

  public static final String ID = "send-webhook-message";

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbit;

  public SendWebhookMessageAction(
    AutomationActionData data,
    RabbitClient rabbitClient
  ) {
    super(data);
    this.rabbit = rabbitClient;
  }

  @Override
  public void execute(Iteration iteration) {
    final String recipientId = iteration.recipientId();
    final var template = value("message");
    if (recipientId == null || template.isEmpty()) {
      return;
    }
    final String message = VariableResolver.resolve(template.get(), iteration);
    if (message.isBlank()) {
      return;
    }
    log.info(
      "Executing SendWebhookMessageAction",
      Map.of("recipientId", recipientId, "message", message)
    );
    rabbit.sendCommand(
      new SendDiscordWebhookMessageCommand(recipientId, message)
    );
  }

  @Serdeable
  public static record SendDiscordWebhookMessageCommand(
    String recipientId,
    String message
  ) {}
}
