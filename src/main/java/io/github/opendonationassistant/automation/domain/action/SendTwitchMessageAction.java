package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

public class SendTwitchMessageAction extends AutomationAction {

  public static final String ID = "send-twitch-message";

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbit;

  public SendTwitchMessageAction(
    AutomationActionData data,
    RabbitClient rabbitClient
  ) {
    super(data);
    this.rabbit = rabbitClient;
  }

  @Override
  public void execute(Iteration iteration) {
    final String recipientId = iteration.recipientId();
    final var senderRefreshTokenId = value("senderRefreshTokenId");
    final var recipientTwitchId = value("recipientTwitchId");
    final var message = value("message");
    if (
      recipientId == null ||
      senderRefreshTokenId.isEmpty() ||
      recipientTwitchId.isEmpty() ||
      message.isEmpty()
    ) {
      return;
    }
    log.info(
      "Executing SendTwitchMessageAction",
      Map.of(
        "recipientId",
        recipientId,
        "senderRefreshTokenId",
        senderRefreshTokenId.get(),
        "recipientTwitchId",
        recipientTwitchId.get()
      )
    );
    rabbit.sendCommand(
      new SendTwitchMessageCommand(
        recipientId,
        senderRefreshTokenId.get(),
        recipientTwitchId.get(),
        message.get()
      )
    );
  }

  @Serdeable
  public static record SendTwitchMessageCommand(
    String recipientId,
    String senderRefreshTokenId,
    String recipientTwitchId,
    String message
  ) {}
}
