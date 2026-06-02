package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;

public abstract class AutomationAction {

  private final AutomationActionData data;

  public AutomationAction(AutomationActionData data) {
    this.data = data;
  }

  public AutomationActionData data() {
    return data;
  }

  public abstract void execute(Iteration iteration);

  public static class EmptyAutomationAction extends AutomationAction {

    public EmptyAutomationAction(AutomationActionData data) {
      super(data);
    }

    @Override
    public void execute(Iteration iteration) {}
  }
}
