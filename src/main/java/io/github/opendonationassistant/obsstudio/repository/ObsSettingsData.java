package io.github.opendonationassistant.obsstudio.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import org.jspecify.annotations.Nullable;

@Serdeable
@MappedEntity("obs_settings")
public record ObsSettingsData(
  @Id @MappedProperty(type = DataType.UUID) String id,
  @MappedProperty("recipient_id") String recipientId,
  @MappedProperty("websocket_url") String websocketUrl,
  @MappedProperty("password") @Nullable String password
) {}
