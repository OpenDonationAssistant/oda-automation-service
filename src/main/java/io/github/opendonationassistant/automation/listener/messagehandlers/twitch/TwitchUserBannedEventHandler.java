package io.github.opendonationassistant.automation.listener.messagehandlers.twitch;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.twitch.events.TwitchUserBannedEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import java.util.List;

public class TwitchUserBannedEventHandler
  extends AbstractMessageHandler<TwitchUserBannedEvent> {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  public TwitchUserBannedEventHandler(UIFacade ui, ObjectMapper mapper) {
    super(mapper);
    this.ui = ui;
  }

  @Override
  public void handle(TwitchUserBannedEvent received) throws IOException {
    var event = new Event(
      received.id(),
      "TwitchUserBannedEvent",
      List.of(
        new Variable(
          uuid.generate().toString(),
          "nickname",
          received.nickname(),
          "string"
        )
      )
    );
    ui.sendEvent(received.recipientId(), event);
  }
}
