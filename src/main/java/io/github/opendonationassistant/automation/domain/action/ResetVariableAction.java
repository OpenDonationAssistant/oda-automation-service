package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import java.math.BigDecimal;

public class ResetVariableAction extends AbstractVariableAction {

  public static final String ID = "reset-variable";

  public ResetVariableAction(
    AutomationActionData data,
    String recipientId,
    AutomationVariableRepository variables
  ) {
    super(data, recipientId, variables);
  }

  @Override
  public void execute(Iteration iteration) {
    variable().ifPresent(variable -> {
      if (variable instanceof AutomationNumberVariable number) {
        number.setValue(BigDecimal.ZERO);
      } else if (variable instanceof AutomationStringVariable string) {
        string.update(string.name(), "");
      }
    });
  }
}
