package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.commons.logging.ODALogger;
import java.util.Map;
import java.util.Optional;

public class FilledDonationGoalTrigger extends AutomationTrigger {

  private final ODALogger log = new ODALogger(this);

  public FilledDonationGoalTrigger(Map<String, Object> value) {
    super("donationgoal-filled", value);
  }

  public Optional<String> getWidgetId() {
    return Optional.ofNullable((String) this.getValue().get("widgetId"));
  }

  @Override
  public boolean isTriggered(Goal updatedGoal) {
    if (getWidgetId().isEmpty()) {
      return false;
    }
    log.info(
      "check FilledDonationGoalTrigger",
      Map.of(
        "goal",
        updatedGoal,
        "widgetId",
        getWidgetId().get(),
        "recipientId",
        updatedGoal.getRecipientId()
      )
    );

    boolean isTriggered =
      getWidgetId().get().equals(updatedGoal.getWidgetId()) &&
      (updatedGoal.getAccumulatedAmount().getMajor() >=
        updatedGoal.getRequiredAmount().getMajor());

    log.info(
      "FilledDonationGoalTrigger is triggered",
      Map.of(
        "goal",
        updatedGoal.getGoalId(),
        "widgetId",
        getWidgetId().get(),
        "requiredAmount",
        updatedGoal.getRequiredAmount().getMajor(),
        "accumulatedAmount",
        updatedGoal.getAccumulatedAmount().getMajor()
      )
    );

    return isTriggered;
  }
}
