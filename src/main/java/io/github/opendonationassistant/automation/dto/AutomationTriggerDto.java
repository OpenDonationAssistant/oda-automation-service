package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

@Serdeable
public record AutomationTriggerDto(String id, Map<String, Object> value) {

  public AutomationTrigger asDomain() {
    return new AutomationTrigger(id, value);
  }
}
