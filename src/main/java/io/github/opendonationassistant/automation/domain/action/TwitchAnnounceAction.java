package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class TwitchAnnounceAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbitClient;

  public TwitchAnnounceAction(
    AutomationActionData data,
    RabbitClient rabbitClient
  ) {
    super(data);
    this.rabbitClient = rabbitClient;
  }

  @Override
  public void execute(Iteration iteration) {
    final String refreshTokenId = (String) data()
      .value()
      .get("senderRefreshTokenId");
    final String recipientTwitchId = (String) data()
      .value()
      .get("recipientTwitchId");
    final String moderatorTwitchId = (String) data()
      .value()
      .get("moderatorTwitchId");
    final String message = (String) data().value().get("message");
    final String color = (String) data().value().get("color");
    if (
      iteration.recipientId() == null ||
      refreshTokenId == null ||
      recipientTwitchId == null ||
      moderatorTwitchId == null ||
      message == null
    ) {
      return;
    }
    log.info(
      "Executing TwitchAnnounceAction",
      Map.of(
        "recipientId",
        iteration.recipientId(),
        "senderRefreshTokenId",
        refreshTokenId,
        "recipientTwitchId",
        recipientTwitchId,
        "moderatorTwitchId",
        moderatorTwitchId,
        "message",
        message,
        "color",
        color
      )
    );
    rabbitClient.sendCommand(
      new TwitchAnnounceCommand(
        iteration.recipientId(),
        refreshTokenId,
        recipientTwitchId,
        moderatorTwitchId,
        message,
        color
      )
    );
  }

  @Serdeable
  public static record TwitchAnnounceCommand(
    String recipientId,
    String senderRefreshTokenId,
    String recipientTwitchId,
    String moderatorTwitchId,
    String message,
    @Nullable String color
  ) {}
}

