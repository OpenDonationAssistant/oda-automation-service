package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.api.Widget;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilledDonationGoalTrigger extends AutomationTrigger {

  private Logger log = LoggerFactory.getLogger(FilledDonationGoalTrigger.class);
  private final WidgetsApi widgets;

  public FilledDonationGoalTrigger(
    Map<String, Object> value,
    WidgetsApi widgets
  ) {
    super("donationgoal-filled", value);
    this.widgets = widgets;
  }

  @Override
  public boolean isTriggered(UpdatedGoal goal) {
    log.info("FilledDonationGoalTrigger is checking goal {}", goal.getGoalId());
    var isTriggered =
      (goal.getAccumulatedAmount().getMajor() >=
        goal.getRequiredAmount().getMajor());
    if (isTriggered) {
      log.info(
        "FilledDonationGoalTrigger is triggered for goal {} with required and collected",
        goal.getGoalId(),
        goal.getRequiredAmount().getMajor(),
        goal.getAccumulatedAmount().getMajor()
      );
    }
    return isTriggered;
  }
}
