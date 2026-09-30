package io.github.opendonationassistant.obsstudio.dto;

import io.github.opendonationassistant.obsstudio.repository.ObsSettingsData;
import io.micronaut.serde.annotation.Serdeable;
import org.jspecify.annotations.Nullable;

@Serdeable
public record ObsSettingsDto(
  String websocketUrl,
  @Nullable String password
) {
  public static ObsSettingsDto from(ObsSettingsData data) {
    return new ObsSettingsDto(data.websocketUrl(), data.password());
  }
}
