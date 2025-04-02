package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.math.BigDecimal;

@Serdeable
@EqualsAndHashCode
public class AutomationVariableDto {

  private String id;
  private String name;
  private String type;
  private String value;

  public AutomationVariableDto(
    String id,
    String name,
    String type,
    String value
  ) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

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

  @Override
  public String toString() {
    try {
      return ObjectMapper.getDefault().writeValueAsString(this);
    } catch (Exception e) {
      return "Can't serialize as  json";
    }
  }

  @Override
  public boolean equals(Object o) {
    return AutomationVariableDtoObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationVariableDtoObject.hashCode(this);
  }

  public String getType() {
    return type;
  }

  public void setType(String type){
    this.type = type;
  }

  public String getValue() {
    return value;
  }
}
