package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilledDonationGoalTrigger extends AutomationTrigger {

  private Logger log = LoggerFactory.getLogger(FilledDonationGoalTrigger.class);

  public FilledDonationGoalTrigger(Map<String, Object> value) {
    super("donationgoal-filled", value);
  }

  @Override
  public boolean isTriggered(UpdatedGoal goal) {
    log.info("FilledDonationGoalTrigger is checking goal {}", goal.getGoalId());
    var isTriggered =
      (goal.getAccumulatedAmount().getMajor() >=
        goal.getRequiredAmount().getMajor());
    log.info("FilledDonationGoalTrigger is triggered");
    return isTriggered;
  }
}
