package io.github.opendonationassistant.automation.domain.variable;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import java.math.BigDecimal;

public class AutomationNumberVariable extends AutomationVariable<BigDecimal> {

  public AutomationNumberVariable(
    AutomationVariableData data,
    AutomationVariableDataRepository repository
  ) {
    super(data, repository);
  }

  public BigDecimal value() {
    if (data().value().isEmpty()) {
      return BigDecimal.ZERO;
    }
    return new BigDecimal(data().value());
  }

  public void setValue(BigDecimal value) {
    super.update(
      new AutomationVariableData(
        data().id(),
        data().type(),
        data().name(),
        data().recipientId(),
        value.toString()
      )
    );
  }
}
