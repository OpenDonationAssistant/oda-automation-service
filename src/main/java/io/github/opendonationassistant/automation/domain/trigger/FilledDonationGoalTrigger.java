package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.api.Widget;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
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
  public boolean isTriggered(UpdatedGoal updatedGoal) {
    log.info(
      "FilledDonationGoalTrigger is checking goal {}, widget {}",
      updatedGoal.getGoalId(),
      updatedGoal.getWidgetId()
    );

    final Widget widget = widgets.getWidget(updatedGoal.getWidgetId()).join();
    final Map<String, Object> config = widget.getConfig();
    final Stream<Map<String, Object>> existingGoals =
      ((List<Map<String, Object>>) config.get("properties")).stream()
        .filter(prop -> "goal".equals(prop.get("name")))
        .flatMap(goal ->
          ((List<Map<String, Object>>) goal.get("value")).stream()
        );

    boolean isTriggered = existingGoals
      .filter(goal -> updatedGoal.getGoalId().equals(goal.get("id")))
      .map(goal -> {
        final Map<String, Object> required =
          ((Map<String, Object>) goal.getOrDefault("requiredAmount", Map.of()));
        Integer requiredAmount = (Integer) required.getOrDefault("major", 0);
        final Map<String, Object> collected =
          ((Map<String, Object>) goal.getOrDefault(
              "accumulatedAmount",
              Map.of()
            ));
        Integer collectedAmount = (Integer) collected.getOrDefault("major", 0);
        if (collectedAmount > requiredAmount) {
          log.info(
            "FilledDonationGoalTrigger is triggered for goal {} with required {} and collected {}",
            updatedGoal.getGoalId(),
            requiredAmount,
            collectedAmount
          );
        }
        return collectedAmount > requiredAmount;
      })
      .filter(result -> result)
      .findAny()
      .isPresent();

    return isTriggered;
  }
}
