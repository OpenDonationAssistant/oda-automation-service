package io.github.opendonationassistant.automation.domain.variable;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;

public class AutomationStringVariable extends AutomationVariable<String> {

  public AutomationStringVariable(
    AutomationVariableData data,
    AutomationVariableDataRepository repository
  ) {
    super(data, repository);
  }

  @Override
  public void update(String name, String value) {
    update(
      new AutomationVariableData(
        data().id(),
        data().type(),
        name,
        data().recipientId(),
        value.toString()
      )
    );
  }
}
