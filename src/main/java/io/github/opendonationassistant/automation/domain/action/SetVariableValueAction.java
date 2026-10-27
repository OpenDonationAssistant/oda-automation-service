package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import java.math.BigDecimal;
import java.util.Optional;

public class SetVariableValueAction extends AbstractVariableAction {

  public static final String ID = "set-variable-value";

  public SetVariableValueAction(
    AutomationActionData data,
    String recipientId,
    AutomationVariableRepository variables
  ) {
    super(data, recipientId, variables);
  }

  public Optional<String> getValue() {
    return Optional.ofNullable(data().value().get("value")).map(String::valueOf);
  }

  @Override
  public void execute(Iteration iteration) {
    var raw = getValue();
    if (raw.isEmpty()) {
      return;
    }
    var value = raw.get();
    variable().ifPresent(variable -> {
      if (variable instanceof AutomationNumberVariable number) {
        try {
          number.setValue(new BigDecimal(value.trim()));
        } catch (NumberFormatException ignored) {
          // invalid numeric payload — leave the variable unchanged
        }
      } else if (variable instanceof AutomationStringVariable string) {
        string.update(string.name(), value);
      }
    });
  }
}
