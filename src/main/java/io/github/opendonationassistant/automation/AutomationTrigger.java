package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.automation.dto.AutomationTriggerDto;
import io.github.opendonationassistant.automation.repository.AutomationRuleData.AutomationTriggerData;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.util.Map;

@Serdeable
@EqualsAndHashCode
public class AutomationTrigger {

  private String id;
  private Map<String, Object> value;

  public AutomationTrigger(String id, Map<String, Object> value) {
    this.id = id;
    this.value = value;
  }

  public boolean isTriggered(Goal goal) {
    return false;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, Object> getValue() {
    return value;
  }

  public AutomationTriggerData asData() {
    return new AutomationTriggerData(this.getId(), this.getValue());
  }

  public AutomationTriggerDto asDto() {
    return new AutomationTriggerDto(this.getId(), this.getValue());
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
    return AutomationTriggerObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationTriggerObject.hashCode(this);
  }
}
