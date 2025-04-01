package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import java.util.Map;

public class FilledDonationGoalTrigger extends AutomationTrigger {

  public FilledDonationGoalTrigger(Map<String, Object> value) {
    super("donationgoal-filled", value);
  }

  @Override
  public boolean isTriggered(UpdatedGoal goal) {
    return (
      goal.getAccumulatedAmount().getMajor() >
      goal.getRequiredAmount().getMajor()
    );
  }
}
