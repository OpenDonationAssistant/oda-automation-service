package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import java.math.BigDecimal;

public class DecreaseVariableAction extends AbstractVariableAction {

  public static final String ID = "decrease-variable";

  public DecreaseVariableAction(
    AutomationActionData data,
    String recipientId,
    AutomationVariableRepository variables
  ) {
    super(data, recipientId, variables);
  }

  @Override
  public void execute(Iteration iteration) {
    numberVariable()
      .ifPresent(variable ->
        variable.setValue(
          variable.value().subtract(new BigDecimal(getAmount().orElse(0)))
        )
      );
  }
}
