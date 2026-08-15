package io.github.opendonationassistant.scheduledrun.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Serdeable
@MappedEntity("scheduled_run")
public record ScheduledRunData(
  @Id UUID id,
  String name,
  Instant time,
  @MappedProperty(type = DataType.JSON) Map<String, Object> schedule,
  boolean done
) {}
