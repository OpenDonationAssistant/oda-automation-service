package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;

@Serdeable
public record AutomationVariableDto(
  String id,
  String name,
  String type,
  String value
) {
  public AutomationVariable<?> asDomain(
    String recipientId,
    AutomationVariableDataRepository repository
  ) {
    if ("number".equals(type)) {
      return new AutomationNumberVariable(
        recipientId,
        id,
        name,
        new BigDecimal(value),
        repository
      );
    }
    return new AutomationStringVariable(
      recipientId,
      id,
      name,
      value,
      repository
    );
  }
}
