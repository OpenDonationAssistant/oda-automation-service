package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilledDonationGoalTrigger extends AutomationTrigger {

  private Logger log = LoggerFactory.getLogger(FilledDonationGoalTrigger.class);

  public FilledDonationGoalTrigger(Map<String, Object> value) {
    super("donationgoal-filled", value);
  }

  @Override
  public boolean isTriggered(Goal updatedGoal) {
    log.info(
      "FilledDonationGoalTrigger is checking goal {}, widget {}, recipient {}",
      updatedGoal.getGoalId(),
      updatedGoal.getWidgetId(),
      updatedGoal.getRecipientId()
    );

    boolean isTriggered =
      updatedGoal.getAccumulatedAmount().getMajor() >=
      updatedGoal.getRequiredAmount().getMajor();

    log.info(
      "FilledDonationGoalTrigger is triggered for goal {} with required {} and collected {}",
      updatedGoal.getGoalId(),
      updatedGoal.getRequiredAmount().getMajor(),
      updatedGoal.getAccumulatedAmount().getMajor()
    );

    return isTriggered;
  }
}
