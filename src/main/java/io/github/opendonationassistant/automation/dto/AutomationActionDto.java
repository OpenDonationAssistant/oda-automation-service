package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationAction;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.util.Map;

@Serdeable
@EqualsAndHashCode
public class AutomationActionDto {

  private String id;
  private Map<String, Object> value;

  public AutomationActionDto(String id, Map<String, Object> value) {
    this.id = id;
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public Map<String, Object> getValue() {
    return this.value;
  }

  public AutomationAction asDomain() {
    return new AutomationAction(this.getId(), this.getValue());
  }

  @Override
  public boolean equals(Object o) {
    return AutomationActionDtoObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationActionDtoObject.hashCode(this);
  }
}
