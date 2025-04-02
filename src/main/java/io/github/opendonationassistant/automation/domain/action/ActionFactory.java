package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import jakarta.inject.Singleton;

import java.util.Map;

@Singleton
public class ActionFactory {

  public AutomationAction create(String id, Map<String, Object> value) {
    return switch (id) {
      case "increase-donation-goal" -> new AutomationAction(id, value);
      case "refresh-donation-goal" -> new RefreshDonationGoalAction(id, value);
      case "increase-variable" -> new AutomationAction(id, value);
      default -> new AutomationAction(id, value);
    };
  }
}
