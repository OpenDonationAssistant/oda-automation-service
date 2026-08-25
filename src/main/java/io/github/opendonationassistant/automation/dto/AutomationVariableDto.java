package io.github.opendonationassistant.automation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record AutomationVariableDto(
  @NotBlank String id,
  @NotNull String name,
  @NotBlank String type,
  @NotNull String value
) {}
