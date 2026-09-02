package io.github.opendonationassistant.automation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Serdeable
public record AutomationTriggerDto(
  @NotBlank String id,
  Map<String, Object> value
) {}
