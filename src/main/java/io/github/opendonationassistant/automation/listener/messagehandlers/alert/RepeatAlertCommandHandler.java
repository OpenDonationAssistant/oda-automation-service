package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import io.github.opendonationassistant.alert.repository.Alert;
import io.github.opendonationassistant.alert.repository.AlertLinkRepository;
import io.github.opendonationassistant.alert.repository.AlertRepository;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Singleton;
import java.io.IOException;
import org.jspecify.annotations.Nullable;

@Singleton
public class RepeatAlertCommandHandler
  extends AbstractMessageHandler<
    io.github.opendonationassistant.automation.listener.messagehandlers.alert.RepeatAlertCommandHandler.RepeatAlertCommand
  > {

  private final AlertRepository repository;
  private final AlertLinkRepository linkRepository;

  public RepeatAlertCommandHandler(
    ObjectMapper mapper,
    AlertRepository repository,
    AlertLinkRepository linkRepository
  ) {
    super(mapper);
    this.repository = repository;
    this.linkRepository = linkRepository;
  }

  @Override
  public void handle(RepeatAlertCommand message) throws IOException {
    if (message.alertId() != null) {
      repository.get(message.alertId()).ifPresent(Alert::send);
    } else if (message.originId() != null) {
      linkRepository
        .getByOriginId(message.originId())
        .forEach(link -> {
          repository.get(link.alertId()).ifPresent(Alert::send);
        });
    }
  }

  @Serdeable
  public static record RepeatAlertCommand(
    @Nullable String alertId,
    @Nullable String originId
  ) {}
}

