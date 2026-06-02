package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;

public abstract class AutomationTrigger {

  private final AutomationTriggerData data;

  public AutomationTrigger(AutomationTriggerData data) {
    this.data = data;
  }

  public AutomationTriggerData data() {
    return data;
  }

  public abstract boolean isTriggered(Object target);

  public abstract void extractVariables(Object target, Iteration iteration);

  public static class NeverTrigger extends AutomationTrigger {

    public NeverTrigger(AutomationTriggerData data) {
      super(data);
    }

    @Override
    public boolean isTriggered(Object target) {
      return false;
    }

    @Override
    public void extractVariables(Object target, Iteration iteration) {}
  }
}
