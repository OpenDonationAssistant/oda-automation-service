package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.AutomationTrigger.NeverTrigger;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class TriggerFactory {

  public AutomationTrigger create(String id, Map<String, Object> value) {
    return switch (id) {
      case "donationgoal-filled" -> new FilledDonationGoalTrigger(
        new AutomationTriggerData(id, value)
      );
      default -> new NeverTrigger(new AutomationTriggerData(id, value));
    };
  }

  public AutomationTrigger from(AutomationTriggerData data) {
    return switch (data.id()) {
      case "donationgoal-filled" -> new FilledDonationGoalTrigger(data);
      case "stream-started" -> new StreamStartedTrigger(data);
      case "channel-raided" -> new StreamStartedTrigger(data);
      default -> new NeverTrigger(data);
    };
  }
}
