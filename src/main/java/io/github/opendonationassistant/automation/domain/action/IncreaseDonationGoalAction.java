package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import java.util.Map;
import java.util.Optional;

public class IncreaseDonationGoalAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);

  private Goal goal;

  public IncreaseDonationGoalAction(
    String id,
    Map<String, Object> value,
    Goal goal
  ) {
    super(id, value);
    this.goal = goal;
  }

  public Integer getIncreaseAmount() {
    return Optional.ofNullable((Integer) this.getValue().get("amount")).orElse(
      0
    );
  }

  public Optional<String> getWidgetId() {
    return Optional.ofNullable((String) this.getValue().get("widgetId"));
  }

  public Optional<String> getGoalId() {
    return Optional.ofNullable((String) this.getValue().get("goalId"));
  }

  public void execute() {
    log.info(
      "Executing IncreaseDonationGoalAction",
      Map.of(
        "widgetId",
        getWidgetId(),
        "requiredAmount",
        goal.getRequiredAmount().getMajor(),
        "increaseAmount",
        getIncreaseAmount()
      )
    );
    goal.setRequiredAmount(
      new Amount(
        goal.getRequiredAmount().getMajor() + getIncreaseAmount(),
        goal.getRequiredAmount().getMinor(),
        "RUB"
      )
    );
  }
}
