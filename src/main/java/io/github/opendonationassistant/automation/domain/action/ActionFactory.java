package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.automation.domain.reel.ReelCommandSender;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class ActionFactory {

  private final AutomationVariableRepository variables;
  private final ReelCommandSender reelCommandSender;

  @Inject
  public ActionFactory(
    AutomationVariableRepository variables,
    ReelCommandSender reelCommandSender
  ) {
    this.variables = variables;
    this.reelCommandSender = reelCommandSender;
  }

  public AutomationAction create(
    String recipientId,
    String id,
    Map<String, Object> value,
    Goal updatedGoal
  ) {
    return switch (id) {
      case "increase-donation-goal" -> new IncreaseDonationGoalAction(
        id,
        value,
        updatedGoal
      );
      case "refresh-donation-goal" -> new RefreshDonationGoalAction(
        id,
        value,
        updatedGoal
      );
      case "increase-variable" -> new IncreaseVariableAction(
        id,
        value,
        recipientId,
        variables
      );
      case "run-reel" -> new RunReelAction(
        id,
        value,
        recipientId,
        reelCommandSender
      );
      default -> new AutomationAction(id, value);
    };
  }
}
