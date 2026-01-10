package io.github.opendonationassistant.automation.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("automationvariable")
public record AutomationVariableData (
  @Id String id,
  String type,
  String name,
  String recipientId,
  String value
){}
