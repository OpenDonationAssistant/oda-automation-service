package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationAction;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

@Serdeable
public record AutomationActionDto(String id, Map<String, Object> value) {
  public AutomationAction asDomain() {
    return new AutomationAction(id, value);
  }
}
