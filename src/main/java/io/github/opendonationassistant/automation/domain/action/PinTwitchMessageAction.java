package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

public class PinTwitchMessageAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbitClient;

  public PinTwitchMessageAction(
    String id,
    Map<String, Object> value,
    RabbitClient rabbitClient
  ) {
    super(id, value);
    this.rabbitClient = rabbitClient;
  }

  public void execute() {
    final String recipientId = (String) getValue().get("recipientId");
    final String refreshTokenId = (String) getValue().get("refreshTokenId");
    final String message = (String) getValue().get("message");
    log.info(
      "Executing PinTwitchMessageAction",
      Map.of("recipientId", recipientId, "refreshTokenId", refreshTokenId)
    );
    if (recipientId == null || refreshTokenId == null || message == null) {
      return;
    }
    rabbitClient.sendCommand(
      new SendAndPinChatMessageCommand(recipientId, refreshTokenId, message)
    );
  }

  @Serdeable
  public record SendAndPinChatMessageCommand(
    String recipientId,
    String refreshTokenId,
    String message
  ) {}
}
