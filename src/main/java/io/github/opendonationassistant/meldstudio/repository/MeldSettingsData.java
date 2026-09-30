package io.github.opendonationassistant.meldstudio.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("meld_settings")
public record MeldSettingsData(
  @Id @MappedProperty(type = DataType.UUID) String id,
  @MappedProperty("recipient_id") String recipientId,
  @MappedProperty("websocket_url") String websocketUrl
) {}
