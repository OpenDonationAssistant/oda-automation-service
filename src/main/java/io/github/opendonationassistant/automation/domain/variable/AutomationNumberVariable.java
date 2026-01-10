package io.github.opendonationassistant.automation.domain.variable;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.math.BigDecimal;

public class AutomationNumberVariable extends AutomationVariable<BigDecimal> {

  public AutomationNumberVariable(
    @Nonnull String recipientId,
    @Nonnull String id,
    @Nonnull String name,
    @Nullable BigDecimal value,
    @Nonnull AutomationVariableDataRepository repository
  ) {
    super(
      recipientId,
      id,
      name,
      value == null ? BigDecimal.ZERO : value,
      repository
    );
  }

  protected AutomationVariableData extractData() {
    return new AutomationVariableData(
      this.getId(),
      "number",
      this.getName(),
      this.getRecipientId(),
      this.getValue().toPlainString()
    );
  }

  public AutomationVariableDto asDto() {
    return new AutomationVariableDto(
      this.getId(),
      this.getName(),
      "number",
      String.valueOf(this.getValue().intValue())
    );
  }
}
