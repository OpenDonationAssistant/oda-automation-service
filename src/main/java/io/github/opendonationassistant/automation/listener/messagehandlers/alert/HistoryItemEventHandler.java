package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import io.github.opendonationassistant.alert.repository.Alert;
import io.github.opendonationassistant.alert.repository.AlertLinkRepository;
import io.github.opendonationassistant.alert.repository.AlertRepository;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.history.event.HistoryItemEvent;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;

@Singleton
public class HistoryItemEventHandler
  extends AbstractMessageHandler<HistoryItemEvent> {

  private final AlertLinkRepository linkRepository;
  private final AlertRepository repository;

  @Inject
  public HistoryItemEventHandler(
    ObjectMapper mapper,
    AlertLinkRepository linkRepository,
    AlertRepository repository
  ) {
    this.linkRepository = linkRepository;
    this.repository = repository;
    super(mapper);
  }

  @Override
  public void handle(HistoryItemEvent event) throws IOException {
    final var originId = event.originId();
    if (originId == null) {
      return;
    }
    linkRepository
      .getByOriginId(originId)
      .forEach(link -> {
        repository.get(link.alertId()).ifPresent(Alert::send);
      });
  }
}
