package io.github.opendonationassistant.automation.domain.variable;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class AutomationStringVariable extends AutomationVariable<String> {

  public AutomationStringVariable(
    @Nonnull String recipientId,
    @Nonnull String id,
    @Nonnull String name,
    @Nullable String value,
    @Nonnull AutomationVariableDataRepository repository
  ) {
    super(recipientId, id, name, value == null ? "" : value, repository);
  }

  protected AutomationVariableData extractData() {
    return new AutomationVariableData(
      this.getId(),
      "string",
      this.getName(),
      this.getRecipientId(),
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
}
