package io.github.opendonationassistant.alert.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("alert_link")
public record AlertLink(
  @Id String id,
  String alertId,
  String originId,
  String source,
  String event
) {}
