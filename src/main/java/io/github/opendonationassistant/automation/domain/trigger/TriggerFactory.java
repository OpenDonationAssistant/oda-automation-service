package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class TriggerFactory {

  private final WidgetsApi widgets;

  @Inject
  public TriggerFactory(WidgetsApi widgets) {
    this.widgets = widgets;
  }

  public AutomationTrigger create(
    String id,
    Map<String, Object> value
  ) {
    return switch (id) {
      case "donationgoal-filled" -> new FilledDonationGoalTrigger(
        value,
        widgets
      );
      default -> new AutomationTrigger(id, value);
    };
  }
}
