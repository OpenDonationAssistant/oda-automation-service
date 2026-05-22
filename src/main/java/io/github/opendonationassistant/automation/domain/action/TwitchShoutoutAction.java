package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchShoutoutAction extends AutomationAction {

  private Logger log = LoggerFactory.getLogger(TwitchShoutoutAction.class);
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
      "Executing TwitchShoutoutAction, recipientId: {}, targetTwitchId: {}",
      recipientId,
      targetTwitchId
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
