package io.github.opendonationassistant.alert.repository;

import io.github.opendonationassistant.commons.Amount;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import org.jspecify.annotations.Nullable;

@Serdeable
@MappedEntity("alert_data")
public record AlertData(
  @Id String id,
  String recipientId,
  @Nullable String nickname,
  @Nullable String message,
  @Nullable Amount amount,
  @Nullable @MappedProperty(type = DataType.JSON) AlertMedia media,
  @Nullable String levelName,
  @Nullable Integer count
) {
  @Serdeable
  public record AlertMedia(String url) {}
}
