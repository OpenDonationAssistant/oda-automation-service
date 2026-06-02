package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;

public class StreamStartedTrigger extends AutomationTrigger {

  public StreamStartedTrigger(AutomationTriggerData data) {
    super(data);
  }

  @Override
  public boolean isTriggered(Object target) {
    return true;
  }

  @Override
  public void extractVariables(Object target, Iteration iteration) {}
}
