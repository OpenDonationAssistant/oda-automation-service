package io.github.opendonationassistant.automation.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record AutomationVariableDto(
  String id,
  String name,
  String type,
  String value
) {}
