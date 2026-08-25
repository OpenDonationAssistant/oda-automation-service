package io.github.opendonationassistant.automation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Serdeable
public record AutomationActionDto(
  @NotBlank String id,
  @NotNull Map<String, Object> value
) {}
