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
public class TwitchChannelSubscriptionMessageEventHandler
  implements MessageHandler {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public TwitchChannelSubscriptionMessageEventHandler(UIFacade ui) {
    this.ui = ui;
  }

  @Override
  public String type() {
    return "TwitchChannelSubscriptionMessageEvent";
  }

  @Override
  public void handle(byte[] message) throws IOException {
    var received = ObjectMapper.getDefault()
      .readValue(
        message,
        io.github.opendonationassistant.events.twitch.events.TwitchChannelSubscriptionMessageEvent.class
      );
    if (received == null) {
      return;
    }
    var event = new io.github.opendonationassistant.events.UIFacade.Event(
      received.id(),
      "TwitchChannelSubscribeEvent",
      List.of(
        new Variable(
          uuid.generate().toString(),
          "nickname",
          received.username(),
          "string"
        ),
        new Variable(
          uuid.generate().toString(),
          "tier",
          Integer.parseInt(received.tier()),
          "number"
        ),
        new Variable(
          uuid.generate().toString(),
          "message",
          received.message(),
          "string"
        ),
        new Variable(
          uuid.generate().toString(),
          "cumulativeMonths",
          received.cumulativeMonths(),
          "number"
        ),
        new Variable(
          uuid.generate().toString(),
          "totalMonths",
          received.totalMonths(),
          "number"
        ),
        new Variable(
          uuid.generate().toString(),
          "streakMonths",
          received.streakMonths(),
          "number"
        )
      )
    );
    ui.sendEvent(received.recipientId(), event);
  }
}
