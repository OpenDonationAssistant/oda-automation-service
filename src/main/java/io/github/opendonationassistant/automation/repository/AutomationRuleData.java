package io.github.opendonationassistant.automation.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@MappedEntity("automationrule")
@Serdeable
public record AutomationRuleData(
  @Id String id,
  String name,
  String recipientId,
  @MappedProperty(type = DataType.JSON) List<AutomationTriggerData> triggers,
  @MappedProperty(type = DataType.JSON) List<AutomationActionData> actions
) {}
