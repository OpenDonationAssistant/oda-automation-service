package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import java.util.Map;
import java.util.Optional;

public class RefreshDonationGoalAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);

  public RefreshDonationGoalAction(AutomationActionData data) {
    super(data);
  }

  public void execute(Iteration iteration) {
    var trigger = iteration.source();
    if (trigger instanceof Goal goal) {
      var diff =
        goal.getAccumulatedAmount().getMajor() -
        goal.getRequiredAmount().getMajor();
      diff = diff > 0 ? diff : 0;
      // prettier-ignore ON
      log.info("Executing RefreshDonationGoalAction", Map.of(
          "widgetId", getWidgetId(),
          "requiredAmount", goal.getRequiredAmount().getMajor(),
          "accumulatedAmount", goal.getAccumulatedAmount().getMajor(),
          "diff", diff
      ));
      // prettier-ignore OFF
      goal.setAccumulatedAmount(new Amount(diff, 0, "RUB"));
    }
  }

  private Optional<String> getWidgetId() {
    return Optional.ofNullable((String) this.data().value().get("widgetId"));
  }
}
