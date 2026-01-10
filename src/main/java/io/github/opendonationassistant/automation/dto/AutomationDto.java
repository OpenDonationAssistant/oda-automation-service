package io.github.opendonationassistant.automation.dto;

import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record AutomationDto(
  List<AutomationRuleDto> rules,
  List<AutomationVariableDto> variables
) {}
