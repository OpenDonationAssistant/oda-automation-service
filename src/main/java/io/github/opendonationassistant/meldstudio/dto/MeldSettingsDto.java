package io.github.opendonationassistant.meldstudio.dto;

import io.github.opendonationassistant.meldstudio.repository.MeldSettingsData;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record MeldSettingsDto(String websocketUrl) {
  public static MeldSettingsDto from(MeldSettingsData data) {
    return new MeldSettingsDto(data.websocketUrl());
  }
}
