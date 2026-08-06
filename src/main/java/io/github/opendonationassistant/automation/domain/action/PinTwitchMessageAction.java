package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

public class PinTwitchMessageAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbitClient;

  public PinTwitchMessageAction(
    AutomationActionData data,
    RabbitClient rabbitClient
  ) {
    super(data);
    this.rabbitClient = rabbitClient;
  }

  @Override
  public void execute(Iteration iteration) {
    final String recipientId = (String) data().value().get("recipientId");
    final String refreshTokenId = (String) data()
      .value()
      .get("senderRefreshTokenId");
    final String recipientTwitchId = (String) data()
      .value()
      .get("recipientTwitchId");
    final String message = (String) data().value().get("message");
    log.info(
      "Executing PinTwitchMessageAction",
      Map.of(
        "recipientId",
        recipientId,
        "senderRefreshTokenId",
        refreshTokenId,
        "recipientTwitchId",
        recipientTwitchId,
        "message",
        message
      )
    );
    if (
      recipientId == null ||
      refreshTokenId == null ||
      recipientTwitchId == null ||
      message == null
    ) {
      return;
    }
    rabbitClient.sendCommand(
      new SendAndPinChatMessageCommand(
        recipientId,
        refreshTokenId,
        recipientTwitchId,
        message
      )
    );
  }

  @Serdeable
  public static record SendAndPinChatMessageCommand(
    String recipientId,
    String senderRefreshTokenId,
    String recipientTwitchId,
    String message
  ) {}
}
