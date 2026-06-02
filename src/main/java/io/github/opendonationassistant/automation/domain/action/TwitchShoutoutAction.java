package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

public class TwitchShoutoutAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbitClient;

  public TwitchShoutoutAction(
    AutomationActionData data,
    RabbitClient rabbitClient
  ) {
    super(data);
    this.rabbitClient = rabbitClient;
  }

  @Override
  public void execute(Iteration iteration) {
    final String targetTwitchId = (String) data().value().get("targetTwitchId");
    // prettier-ignore ON
    log.info("Executing TwitchShoutoutAction", Map.of(
        "recipientId", iteration.recipientId(),
        "targetTwitchId", targetTwitchId
    ));
    // prettier-ignore OFF

    if (targetTwitchId == null) {
      return;
    }
    rabbitClient.sendCommand(
      new TwitchShoutoutCommand(iteration.recipientId(), targetTwitchId)
    );
  }

  @Serdeable
  public record TwitchShoutoutCommand(
    String recipientId,
    String targetTwitchId
  ) {}
}
