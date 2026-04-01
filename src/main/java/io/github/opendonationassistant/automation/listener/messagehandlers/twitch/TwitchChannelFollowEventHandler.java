package io.github.opendonationassistant.automation.listener.messagehandlers.twitch;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.twitch.events.TwitchChannelFollowEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class TwitchChannelFollowEventHandler extends AbstractMessageHandler<TwitchChannelFollowEvent> {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public TwitchChannelFollowEventHandler(UIFacade ui, ObjectMapper mapper) {
    super(mapper);
    this.ui = ui;
  }

  @Override
  public void handle(TwitchChannelFollowEvent received) throws IOException {
    var event = new Event(
      received.id(),
      "TwitchChannelFollowEvent",
      List.of(
        new Variable(
          uuid.generate().toString(),
          "nickname",
          received.username(),
          "string"
        )
      )
    );
    ui.sendEvent(received.recipientId(), event);
  }
}
