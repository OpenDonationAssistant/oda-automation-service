package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import java.util.Map;
import java.util.Optional;

public class RefreshDonationGoalAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);

  private final Goal goal;

  public RefreshDonationGoalAction(
    String id,
    Map<String, Object> value,
    Goal goal
  ) {
    super(id, value);
    this.goal = goal;
  }

  public void execute() {
    var diff =
      goal.getAccumulatedAmount().getMajor() -
      goal.getRequiredAmount().getMajor();
    diff = diff > 0 ? diff : 0;
    log.info(
      "Executing RefreshDonationGoalAction: {}, goal: {}, current amount: {}, diff: {}",
      Map.of(
        "widgetId",
        getWidgetId(),
        "requiredAmount",
        goal.getRequiredAmount().getMajor(),
        "accumulatedAmount",
        goal.getAccumulatedAmount().getMajor(),
        "diff",
        diff
      )
    );
    goal.setAccumulatedAmount(new Amount(diff, 0, "RUB"));
  }

  private Optional<String> getWidgetId() {
    return Optional.ofNullable((String) this.getValue().get("widgetId"));
  }
}
