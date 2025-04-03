package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class IncreaseVariableAction extends AutomationAction {

  private String recipientId;
  private AutomationVariableRepository variables;

  public IncreaseVariableAction(
    String id,
    Map<String, Object> value,
    String recipientId,
    AutomationVariableRepository variables
  ) {
    super(id, value);
    this.recipientId = recipientId;
    this.variables = variables;
  }

  public Optional<String> getVariableId() {
    return Optional.ofNullable((String) this.getValue().get("id"));
  }

  public Optional<Integer> getAmount() {
    return Optional.ofNullable((Integer) this.getValue().get("value"));
  }

  public void execute() {
    getVariableId()
      .flatMap(id -> variables.getById(recipientId, id))
      .filter(variable -> variable instanceof AutomationNumberVariable)
      .map(variable -> (AutomationNumberVariable) variable)
      .ifPresent(variable ->
        variable.setValue(
          variable.getValue().add(new BigDecimal(getAmount().orElse(0)))
        )
      );
  }
}
