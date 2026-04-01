package io.github.opendonationassistant.automation.listener.messagehandlers.twitch;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.twitch.events.TwitchChannelSubscriptionMessageEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class TwitchChannelSubscriptionMessageEventHandler
  extends AbstractMessageHandler<TwitchChannelSubscriptionMessageEvent> {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public TwitchChannelSubscriptionMessageEventHandler(UIFacade ui, ObjectMapper mapper) {
    super(mapper);
    this.ui = ui;
  }

  @Override
  public void handle(TwitchChannelSubscriptionMessageEvent received) throws IOException {
    var event = new Event(
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
