package io.github.opendonationassistant.automation.domain.variable;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.dto.AutomationNumberVariableDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.math.BigDecimal;

public class AutomationNumberVariable extends AutomationVariable<BigDecimal> {

  private BigDecimal value;

  public AutomationNumberVariable(
    @Nonnull String recipientId,
    @Nonnull String id,
    @Nonnull String name,
    @Nullable BigDecimal value,
    @Nonnull AutomationVariableDataRepository repository
  ) {
    super(recipientId, id, name, repository);
    this.value = value == null ? BigDecimal.ZERO : value;
  }

  protected AutomationVariableData extractData() {
    return new AutomationVariableData(
      this.getRecipientId(),
      "number",
      this.getId(),
      this.getName(),
      this.value.toPlainString()
    );
  }

  public AutomationVariableDto asDto() {
    return new AutomationNumberVariableDto(
      this.getId(),
      this.getName(),
      this.getValue().intValue()
    );
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }
}
