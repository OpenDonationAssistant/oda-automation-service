package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

public class SendKickMessageAction extends AutomationAction {

  public static final String ID = "send-kick-message";

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbit;

  public SendKickMessageAction(
    AutomationActionData data,
    RabbitClient rabbitClient
  ) {
    super(data);
    this.rabbit = rabbitClient;
  }

  @Override
  public void execute(Iteration iteration) {
    final String recipientId = iteration.recipientId();
    final var refreshTokenId = value("refreshTokenId");
    final var recipientKickId = value("recipientKickId");
    final var message = value("message");
    if (
      recipientId == null ||
      refreshTokenId.isEmpty() ||
      recipientKickId.isEmpty() ||
      message.isEmpty()
    ) {
      return;
    }
    log.info(
      "Executing SendKickMessageAction",
      Map.of(
        "recipientId",
        recipientId,
        "refreshTokenId",
        refreshTokenId.get(),
        "recipientKickId",
        recipientKickId.get()
      )
    );
    rabbit.sendCommand(
      new SendKickMessageCommand(
        recipientId,
        refreshTokenId.get(),
        recipientKickId.get(),
        message.get()
      )
    );
  }

  @Serdeable
  public static record SendKickMessageCommand(
    String recipientId,
    String refreshTokenId,
    String recipientKickId,
    String message
  ) {}
}
