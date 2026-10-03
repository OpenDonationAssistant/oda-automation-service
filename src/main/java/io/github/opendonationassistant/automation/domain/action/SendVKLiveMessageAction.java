package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

public class SendVKLiveMessageAction extends AutomationAction {

  public static final String ID = "send-vklive-message";

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbit;

  public SendVKLiveMessageAction(
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
    final var recipientVkId = value("recipientVkId");
    final var message = value("message");
    if (
      recipientId == null ||
      senderRefreshTokenId.isEmpty() ||
      recipientVkId.isEmpty() ||
      message.isEmpty()
    ) {
      return;
    }
    log.info(
      "Executing SendVKLiveMessageAction",
      Map.of(
        "recipientId",
        recipientId,
        "senderRefreshTokenId",
        senderRefreshTokenId.get(),
        "recipientVkId",
        recipientVkId.get()
      )
    );
    rabbit.sendCommand(
      new SendVKLiveMessageCommand(
        recipientId,
        senderRefreshTokenId.get(),
        recipientVkId.get(),
        message.get()
      )
    );
  }

  @Serdeable
  public static record SendVKLiveMessageCommand(
    String recipientId,
    String senderRefreshTokenId,
    String recipientVkId,
    String message
  ) {}
}
