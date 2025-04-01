package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;

@Serdeable
@EqualsAndHashCode
public class AutomationStringVariableDto extends AutomationVariableDto {

  private String value;

  public AutomationStringVariableDto(String id, String name, String value) {
    super(id, name);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public AutomationVariable<?> asDomain(
    String recipientId,
    AutomationVariableDataRepository repository
  ) {
    var variable = new AutomationStringVariable(
      recipientId,
      this.getId(),
      this.getName(),
      this.getValue(),
      repository
    );
    return variable;
  }

  @Override
  public boolean equals(Object o) {
    return AutomationStringVariableDtoObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationStringVariableDtoObject.hashCode(this);
  }
}
