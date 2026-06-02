package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.github.opendonationassistant.commons.logging.ODALogger;
import java.util.Map;

public class AutomationVariable<T> {

  private static final ODALogger log = new ODALogger(AutomationVariable.class);

  private AutomationVariableData data;
  private final AutomationVariableDataRepository repository;

  public AutomationVariable(
    AutomationVariableData data,
    AutomationVariableDataRepository repository
  ) {
    this.data = data;
    this.repository = repository;
  }

  public AutomationVariableData data() {
    return data;
  }

  public T value() {
    return (T) data.value();
  }

  public void update(String name, T value) {
    update(
      new AutomationVariableData(
        data.id(),
        data.type(),
        name,
        data.recipientId(),
        value.toString()
      )
    );
  }

  protected void update(AutomationVariableData data) {
    this.data = data;
    this.save();
  }

  public void save() {
    log.debug("Updating variable", Map.of("updated", data));
    repository.update(data);
  }

  public void delete() {
    repository.deleteById(this.data.id());
  }

  public static enum Scope {
    GLOBAL,
    RULE,
  }
}
