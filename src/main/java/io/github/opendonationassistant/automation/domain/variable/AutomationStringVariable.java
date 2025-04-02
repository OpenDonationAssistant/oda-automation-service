package io.github.opendonationassistant.automation.domain.variable;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class AutomationStringVariable extends AutomationVariable<String> {

  private String value;

  public AutomationStringVariable(
    @Nonnull String recipientId,
    @Nonnull String id,
    @Nonnull String name,
    @Nullable String value,
    @Nonnull AutomationVariableDataRepository repository
  ) {
    super(recipientId, id, name, repository);
    this.value = value == null ? "" : value;
  }

  protected AutomationVariableData extractData() {
    return new AutomationVariableData(
      this.getRecipientId(),
      "string",
      this.getId(),
      this.getName(),
      this.getValue()
    );
  }

  public AutomationVariableDto asDto() {
    return new AutomationVariableDto(
      this.getId(),
      this.getName(),
      "string",
      this.getValue()
    );
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
