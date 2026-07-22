package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import io.github.opendonationassistant.alert.repository.AlertData;
import io.github.opendonationassistant.alert.repository.AlertDataRepository;
import io.github.opendonationassistant.alert.repository.AlertLinkRepository;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.history.event.DeletedHistoryItem;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;

@Singleton
public class DeletedHistoryItemHandler
  extends AbstractMessageHandler<DeletedHistoryItem> {

  private final AlertLinkRepository linkRepository;
  private final AlertDataRepository dataRepository;

  @Inject
  public DeletedHistoryItemHandler(
    ObjectMapper mapper,
    AlertLinkRepository linkRepository,
    AlertDataRepository dataRepository
  ) {
    super(mapper);
    this.linkRepository = linkRepository;
    this.dataRepository = dataRepository;
  }

  @Override
  public void handle(DeletedHistoryItem message) throws IOException {
    final var originId = message.originId();
    if (originId == null) {
      return;
    }
    linkRepository
      .getByOriginId(originId)
      .forEach(link -> {
        dataRepository.findById(link.alertId()).ifPresent(data -> {
          var updated = new AlertData(
            data.id(),
            data.recipientId(),
            data.nickname(),
            data.message(),
            data.amount(),
            data.media(),
            data.levelName(),
            data.count(),
            data.createdAt(),
            true
          );
          dataRepository.save(updated);
        });
      });
  }
}
