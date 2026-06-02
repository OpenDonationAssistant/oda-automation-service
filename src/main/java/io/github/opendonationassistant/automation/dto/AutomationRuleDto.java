package io.github.opendonationassistant.automation.dto;

import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record AutomationRuleDto(
  String id,
  String name,
  List<AutomationTriggerDto> triggers,
  List<AutomationActionDto> actions
) {}
