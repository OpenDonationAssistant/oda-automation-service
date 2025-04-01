package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.util.Map;

@Serdeable
@EqualsAndHashCode
public class AutomationTriggerDto {

  private String id;
  private Map<String, Object> value;

  public AutomationTriggerDto(String id, Map<String, Object> value) {
    this.id = id;
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public Map<String, Object> getValue() {
    return value;
  }

  public AutomationTrigger asDomain() {
    return new AutomationTrigger(this.getId(), this.getValue());
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
    return AutomationTriggerDtoObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationTriggerDtoObject.hashCode(this);
  }
}
