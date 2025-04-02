package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class TriggerFactory {

  public AutomationTrigger create(String id, Map<String, Object> value) {
    return switch (id) {
      case "donationgoal-filled" -> new FilledDonationGoalTrigger(value);
      default -> new AutomationTrigger(id, value);
    };
  }

}
