package io.github.opendonationassistant.obsstudio.repository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

@Singleton
public class ObsSettingsRepository {

  private final ObsSettingsDataRepository dataRepository;

  @Inject
  public ObsSettingsRepository(ObsSettingsDataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  public Optional<ObsSettingsData> getByRecipientId(String recipientId) {
    return dataRepository.getByRecipientId(recipientId);
  }

  public ObsSettingsData upsert(
    String recipientId,
    String websocketUrl,
    @Nullable String password
  ) {
    return dataRepository
      .getByRecipientId(recipientId)
      .map(existing ->
        dataRepository.update(
          new ObsSettingsData(
            existing.id(),
            recipientId,
            websocketUrl,
            password
          )
        )
      )
      .orElseGet(() ->
        dataRepository.save(
          new ObsSettingsData(
            UUID.randomUUID().toString(),
            recipientId,
            websocketUrl,
            password
          )
        )
      );
  }
}
