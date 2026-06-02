package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import java.math.BigDecimal;
import java.util.Optional;

public class IncreaseVariableAction extends AutomationAction {

  private String recipientId;
  private AutomationVariableRepository variables;

  public IncreaseVariableAction(
    AutomationActionData data,
    String recipientId,
    AutomationVariableRepository variables
  ) {
    super(data);
    this.recipientId = recipientId;
    this.variables = variables;
  }

  public Optional<String> getVariableId() {
    return Optional.ofNullable((String) this.data().value().get("id"));
  }

  public Optional<Integer> getAmount() {
    return Optional.ofNullable((Integer) this.data().value().get("value"));
  }

  @Override
  public void execute(Iteration iteration) {
    getVariableId()
      .flatMap(id -> variables.getById(recipientId, id))
      .filter(variable -> variable instanceof AutomationNumberVariable)
      .map(variable -> (AutomationNumberVariable) variable)
      .ifPresent(variable ->
        variable.setValue(
          variable.value().add(new BigDecimal(getAmount().orElse(0)))
        )
      );
  }
}
