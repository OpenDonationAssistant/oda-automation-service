package io.github.opendonationassistant.alert.repository;

import io.github.opendonationassistant.commons.Amount;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import org.jspecify.annotations.Nullable;

@Serdeable
@MappedEntity("alert_data")
public record AlertData(
  @Id String id,
  String recipientId,
  @Nullable String nickname,
  @Nullable String message,
  @Nullable Amount amount
) {}
