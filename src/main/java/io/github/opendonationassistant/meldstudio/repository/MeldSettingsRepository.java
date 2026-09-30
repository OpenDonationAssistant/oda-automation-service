package io.github.opendonationassistant.meldstudio.repository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class MeldSettingsRepository {

  private MeldSettingsDataRepository dataRepository;

  @Inject
  public MeldSettingsRepository(MeldSettingsDataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  public Optional<MeldSettingsData> getByRecipientId(String recipientId) {
    return dataRepository.getByRecipientId(recipientId);
  }

  public MeldSettingsData upsert(String recipientId, String websocketUrl) {
    return dataRepository
      .getByRecipientId(recipientId)
      .map(existing ->
        dataRepository.update(
          new MeldSettingsData(existing.id(), recipientId, websocketUrl)
        )
      )
      .orElseGet(() ->
        dataRepository.save(
          new MeldSettingsData(
            UUID.randomUUID().toString(),
            recipientId,
            websocketUrl
          )
        )
      );
  }
}
