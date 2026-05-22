package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.reel.ReelCommand;
import io.github.opendonationassistant.automation.domain.reel.ReelCommandSender;
import io.github.opendonationassistant.commons.logging.ODALogger;
import java.util.Map;

public class RunReelAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);
  private final ReelCommandSender reelCommandSender;
  private final String recipientId;

  public RunReelAction(
    String id,
    Map<String, Object> value,
    String recipientId,
    ReelCommandSender reelCommandSender
  ) {
    super(id, value);
    this.reelCommandSender = reelCommandSender;
    this.recipientId = recipientId;
  }

  public void execute() {
    final String reelId = (String) getValue().get("reelId");
    log.info(
      "Executing RunReelAction",
      Map.of("reelId", reelId, "recipientId", recipientId)
    );
    if (reelId == null) {
      return;
    }
    reelCommandSender.send(
      "reel",
      new ReelCommand("select", "", reelId, "", recipientId)
    );
  }
}
