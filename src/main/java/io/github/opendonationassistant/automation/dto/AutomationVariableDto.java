package io.github.opendonationassistant.automation.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;

@Serdeable
@JsonTypeInfo(use = Id.NAME)
@JsonSubTypes(
  {
    @Type(AutomationStringVariableDto.class),
    @Type(AutomationNumberVariableDto.class),
  }
)
@EqualsAndHashCode
public abstract class AutomationVariableDto {

  private String id;
  private String name;

  public AutomationVariableDto(String id, String name) {
    this.id = id;
    this.name = name;
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

  public abstract AutomationVariable<?> asDomain(
    String recipientId,
    AutomationVariableDataRepository repository
  );

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
}
