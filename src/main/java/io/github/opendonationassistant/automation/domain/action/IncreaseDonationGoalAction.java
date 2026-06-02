package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import java.util.Map;
import java.util.Optional;

public class IncreaseDonationGoalAction extends AutomationAction {

  private final ODALogger log = new ODALogger(this);

  public IncreaseDonationGoalAction(AutomationActionData data) {
    super(data);
  }

  public Integer getIncreaseAmount() {
    return Optional.ofNullable(
      (Integer) this.data().value().get("amount")
    ).orElse(0);
  }

  public Optional<String> getWidgetId() {
    return Optional.ofNullable((String) this.data().value().get("widgetId"));
  }

  public Optional<String> getGoalId() {
    return Optional.ofNullable((String) this.data().value().get("goalId"));
  }

  @Override
  public void execute(Iteration iteration) {
    var trigger = iteration.source();
    if (trigger instanceof Goal goal) {
      // prettier-ignore ON
      log.info("Executing IncreaseDonationGoalAction", Map.of(
          "widgetId", getWidgetId(),
          "requiredAmount", goal.getRequiredAmount().getMajor(),
          "increaseAmount", getIncreaseAmount()
      ));
      // prettier-ignore OFF
      goal.setRequiredAmount(
        new Amount(
          goal.getRequiredAmount().getMajor() + getIncreaseAmount(),
          goal.getRequiredAmount().getMinor(),
          "RUB"
        )
      );
    }
  }
}
