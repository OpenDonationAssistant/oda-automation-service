package io.github.opendonationassistant.automation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Serdeable
public record AutomationRuleDto(
  @NotBlank String id,
  @NotBlank String name,
  @Valid @NotNull List<AutomationTriggerDto> triggers,
  @Valid @NotNull List<AutomationActionDto> actions,
  @NotNull Boolean enabled
) {}
