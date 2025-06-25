package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.dto.AutomationActionDto;
import io.github.opendonationassistant.automation.repository.AutomationRuleData.AutomationActionData;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.util.Map;

@Serdeable
@EqualsAndHashCode
public class AutomationAction {

  private String id;
  private Map<String, Object> value;

  public AutomationAction(String id, Map<String, Object> value) {
    this.id = id;
    this.value = value;
  }

  public AutomationActionData asData() {
    return new AutomationActionData(this.getId(), this.getValue());
  }

  public AutomationActionDto asDto() {
    return new AutomationActionDto(this.id, this.value);
  }

  public void execute() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, Object> getValue() {
    return value;
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
    return AutomationActionObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationActionObject.hashCode(this);
  }
}
