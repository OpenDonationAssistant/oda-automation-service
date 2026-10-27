package io.github.opendonationassistant.automation.domain.variable;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.github.opendonationassistant.rabbit.RabbitClient;

public class AutomationStringVariable extends AutomationVariable<String> {

  public AutomationStringVariable(
    AutomationVariableData data,
    AutomationVariableDataRepository repository,
    RabbitClient rabbit
  ) {
    super(data, repository, rabbit);
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
