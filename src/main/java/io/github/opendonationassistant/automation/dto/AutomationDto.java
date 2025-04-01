package io.github.opendonationassistant.automation.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.util.List;

@Serdeable
@EqualsAndHashCode
public class AutomationDto {

  private List<AutomationRuleDto> rules;
  private List<AutomationVariableDto> variables;

  public AutomationDto(
    List<AutomationRuleDto> rules,
    List<AutomationVariableDto> variables
  ) {
    this.rules = rules;
    this.variables = variables;
  }

  public List<AutomationVariableDto> getVariables() {
    return variables;
  }

  public List<AutomationRuleDto> getRules() {
    return this.rules;
  }

  @Override
  public boolean equals(Object o) {
    return AutomationDtoObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationDtoObject.hashCode(this);
  }
}
