package io.github.opendonationassistant.automation.listener.messagehandlers;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import io.github.opendonationassistant.events.MessageHandler;
import io.github.opendonationassistant.events.UIFacade;
import io.github.opendonationassistant.events.UIFacade.Variable;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.List;

@Singleton
public class TwitchChannelRaidEventHandler implements MessageHandler {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public TwitchChannelRaidEventHandler(UIFacade ui) {
    this.ui = ui;
  }

  @Override
  public String type() {
    return "TwitchChannelRaidEvent";
  }

  public void handle(byte[] message) throws IOException {
    var received = ObjectMapper.getDefault()
      .readValue(
        message,
        io.github.opendonationassistant.events.twitch.events.TwitchChannelRaidEvent.class
      );
    if (received == null) {
      return;
    }
    var event = new io.github.opendonationassistant.events.UIFacade.Event(
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
