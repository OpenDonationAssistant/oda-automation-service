package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.math.BigDecimal;

@Serdeable
@EqualsAndHashCode
public class AutomationNumberVariableDto extends AutomationVariableDto {

  private Integer value;

  public AutomationNumberVariableDto(String id, String name, Integer value) {
    super(id, name);
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }

  @Override
  public AutomationVariable<?> asDomain(
    String recipientId,
    AutomationVariableDataRepository repository
  ) {
    return new AutomationNumberVariable(
      recipientId,
      this.getId(),
      this.getName(),
      BigDecimal.valueOf(this.getValue()),
      repository
    );
  }

  @Override
  public boolean equals(Object o) {
    return AutomationNumberVariableDtoObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationNumberVariableDtoObject.hashCode(this);
  }
}
