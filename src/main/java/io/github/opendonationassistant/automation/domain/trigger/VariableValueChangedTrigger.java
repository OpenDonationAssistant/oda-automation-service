package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.EphemeralVariable;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import java.util.Map;
import java.util.Optional;

public class VariableValueChangedTrigger extends AutomationTrigger {

  public static final String ID = "variable-value-changed";

  public VariableValueChangedTrigger(AutomationTriggerData data) {
    super(data);
  }

  public Optional<String> getVariableId() {
    return configValue("id").filter(value -> !value.isBlank());
  }

  public Optional<String> getVariableName() {
    return configValue("name").filter(value -> !value.isBlank());
  }

  @Override
  public boolean isTriggered(Object target) {
    if (!(target instanceof AutomationVariable.VariableEvent event)) {
      return false;
    }
    return matchesVariable(event);
  }

  @Override
  public void extractVariables(Object target, Iteration iteration) {
    if (!(target instanceof AutomationVariable.VariableEvent event)) {
      return;
    }
    String value = event.value();
    if (value != null) {
      iteration.add(new EphemeralVariable<String>("value", value));
    }
    String name = event.name();
    if (name != null) {
      iteration.add(new EphemeralVariable<String>("variableName", name));
    }
  }

  private boolean matchesVariable(AutomationVariable.VariableEvent event) {
    var id = getVariableId();
    if (id.isPresent()) {
      return id.get().equals(event.id());
    }
    return getVariableName()
      .map(name -> name.equals(event.name()))
      .orElse(false);
  }

  private Optional<String> configValue(String key) {
    Map<String, Object> values = this.data().value();
    if (values == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(values.get(key)).map(String::valueOf);
  }
}
