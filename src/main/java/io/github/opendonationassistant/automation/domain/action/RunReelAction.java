package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.reel.ReelCommand;
import io.github.opendonationassistant.rabbit.RabbitClient;
import java.util.Map;

public class RunReelAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);
  private final RabbitClient rabbit;
  private final String recipientId;

  public RunReelAction(
    AutomationActionData data,
    String recipientId,
    RabbitClient rabbit
  ) {
    super(data);
    this.rabbit = rabbit;
    this.recipientId = recipientId;
  }

  @Override
  public void execute(Iteration iteration) {
    final String reelId = (String) data().value().get("reelId");
    log.info(
      "Executing RunReelAction",
      Map.of("reelId", reelId, "recipientId", recipientId)
    );
    if (reelId == null) {
      return;
    }
    rabbit.sendCommand(
      new ReelCommand.TriggerReelCommand(
        "", // widgetId
        "", // recipientId
        "", // source
        "" // originId
      )
    );
  }
}
