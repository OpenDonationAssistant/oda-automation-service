package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

public class TwitchShoutoutAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbitClient;

  public TwitchShoutoutAction(
    String id,
    Map<String, Object> value,
    RabbitClient rabbitClient
  ) {
    super(id, value);
    this.rabbitClient = rabbitClient;
  }

  public void execute() {
    final String recipientId = (String) getValue().get("recipientId");
    final String targetTwitchId = (String) getValue().get("targetTwitchId");
    log.info(
      "Executing TwitchShoutoutAction",
      Map.of("recipientId", recipientId, "targetTwitchId", targetTwitchId)
    );
    if (recipientId == null || targetTwitchId == null) {
      return;
    }
    rabbitClient.sendCommand(
      new TwitchShoutoutCommand(recipientId, targetTwitchId)
    );
  }

  @Serdeable
  public record TwitchShoutoutCommand(
    String recipientId,
    String targetTwitchId
  ) {}
}
