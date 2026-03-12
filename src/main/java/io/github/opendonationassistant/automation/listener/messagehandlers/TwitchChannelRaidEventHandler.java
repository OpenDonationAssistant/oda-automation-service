package io.github.opendonationassistant.automation.listener.messagehandlers;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.twitch.events.TwitchChannelRaidEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class TwitchChannelRaidEventHandler
  extends AbstractMessageHandler<TwitchChannelRaidEvent> {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public TwitchChannelRaidEventHandler(UIFacade ui, ObjectMapper mapper) {
    super(mapper);
    this.ui = ui;
  }

  public void handle(TwitchChannelRaidEvent received) throws IOException {
    var event = new Event(
      received.id(),
      "TwitchChannelRaidEvent",
      List.of(
        new Variable(
          uuid.generate().toString(),
          "channel",
          received.fromChannelName(),
          "string"
        ),
        new Variable(
          uuid.generate().toString(),
          "viewerCount",
          received.viewerCount(),
          "number"
        )
      )
    );
    ui.sendEvent(received.recipientId(), event);
  }
}
