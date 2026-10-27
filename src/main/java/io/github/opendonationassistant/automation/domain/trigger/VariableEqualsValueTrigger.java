package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class VariableEqualsValueTrigger extends AutomationTrigger {

  public static final String ID = "variable-equals-value";

  public VariableEqualsValueTrigger(AutomationTriggerData data) {
    super(data);
  }

  public Optional<String> getVariableId() {
    return configValue("id").filter(value -> !value.isBlank());
  }

  public Optional<String> getVariableName() {
    return configValue("name").filter(value -> !value.isBlank());
  }

  public Optional<String> getExpectedValue() {
    return configValue("value");
  }

  @Override
  public boolean isTriggered(Object target) {
    if (!(target instanceof AutomationVariable.VariableEvent event)) {
      return false;
    }
    var expected = getExpectedValue();
    if (expected.isEmpty() || !matchesVariable(event)) {
      return false;
    }
    return matchesValue(event, expected.get());
  }

  @Override
  public void extractVariables(Object target, Iteration iteration) {}

  private boolean matchesVariable(AutomationVariable.VariableEvent event) {
    var id = getVariableId();
    if (id.isPresent()) {
      return id.get().equals(event.id());
    }
    return getVariableName()
      .map(name -> name.equals(event.name()))
      .orElse(false);
  }

  private boolean matchesValue(
    AutomationVariable.VariableEvent event,
    String expected
  ) {
    if ("number".equals(event.type())) {
      try {
        return new BigDecimal(event.value()).compareTo(new BigDecimal(expected)) == 0;
      } catch (NumberFormatException e) {
        return false;
      }
    }
    return event.value().equals(expected);
  }

  private Optional<String> configValue(String key) {
    Map<String, Object> values = this.data().value();
    if (values == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(values.get(key)).map(String::valueOf);
  }
}
