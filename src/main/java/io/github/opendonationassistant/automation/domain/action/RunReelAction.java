package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.automation.domain.reel.ReelCommand;
import io.github.opendonationassistant.automation.domain.reel.ReelCommandSender;
import io.github.opendonationassistant.automation.domain.reel.ReelWidgetCommandSender;
import io.github.opendonationassistant.commons.Amount;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunReelAction extends AutomationAction {

  private Logger log = LoggerFactory.getLogger(RunReelAction.class);
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
      "Executing RunReelAction, reelId: {}, recipientId: {}",
      reelId,
      recipientId
    );
    if (reelId == null) {
      return;
    }
    reelCommandSender.send(
      "reel",
      new ReelCommand(
        "select",
        "",
        reelId,
        "",
        recipientId
      )
    );
  }
}
