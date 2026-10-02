package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import java.util.Optional;

public abstract class AbstractVariableAction extends AutomationAction {

  private final String recipientId;
  private final AutomationVariableRepository variables;

  protected AbstractVariableAction(
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

  protected Optional<AutomationVariable<?>> variable() {
    return getVariableId().flatMap(id -> variables.getById(recipientId, id));
  }

  protected Optional<AutomationNumberVariable> numberVariable() {
    return variable()
      .filter(AutomationNumberVariable.class::isInstance)
      .map(AutomationNumberVariable.class::cast);
  }
}
