package io.github.opendonationassistant.automation.listener.messagehandlers.twitch;

import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.twitch.events.TwitchStreamEndedEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class TwitchStreamEndedEventHandler
  extends AbstractMessageHandler<TwitchStreamEndedEvent> {

  private final UIFacade ui;

  @Inject
  public TwitchStreamEndedEventHandler(UIFacade ui, ObjectMapper mapper) {
    super(mapper);
    this.ui = ui;
  }

  @Override
  public void handle(TwitchStreamEndedEvent received) throws IOException {
    var event = new Event(received.id(), "TwitchStreamEndedEvent", List.of());
    ui.sendEvent(received.recipientId(), event);
  }
}

