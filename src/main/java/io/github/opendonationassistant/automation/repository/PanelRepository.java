package io.github.opendonationassistant.automation.repository;

import io.github.opendonationassistant.automation.repository.PanelData.PanelCardData;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Singleton
public class PanelRepository {

  private PanelDataRepository repository;

  @Inject
  public PanelRepository(PanelDataRepository repository) {
    this.repository = repository;
  }

  public Stream<Panel> listByRecipientId(String recipientId) {
    return repository.getByRecipientId(recipientId).stream().map(this::convert);
  }

  public Optional<Panel> getByRecipientIdAndId(String recipientId, String id) {
    return repository.getByRecipientIdAndId(recipientId, id).map(this::convert);
  }

  public Panel create(
    String recipientId,
    String name,
    List<PanelCardData> cards
  ) {
    PanelData panel = new PanelData(
      UUID.randomUUID().toString(),
      name,
      recipientId,
      cards
    );
    return convert(repository.save(panel));
  }

  private Panel convert(PanelData panel) {
    return new Panel(panel, repository);
  }
}
