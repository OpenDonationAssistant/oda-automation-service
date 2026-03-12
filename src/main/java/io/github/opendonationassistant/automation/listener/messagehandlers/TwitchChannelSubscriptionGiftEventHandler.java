package io.github.opendonationassistant.automation.listener.messagehandlers;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.twitch.events.TwitchChannelSubscriptionGiftEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class TwitchChannelSubscriptionGiftEventHandler
  extends AbstractMessageHandler<TwitchChannelSubscriptionGiftEvent> {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public TwitchChannelSubscriptionGiftEventHandler(UIFacade ui, ObjectMapper mapper) {
    super(mapper);
    this.ui = ui;
  }

  @Override
  public void handle(TwitchChannelSubscriptionGiftEvent received) throws IOException {
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
          "amount",
          received.amount(),
          "number"
        ),
        new Variable(
          uuid.generate().toString(),
          "tier",
          received.tier(),
          "number"
        )
      )
    );
    ui.sendEvent(received.recipientId(), event);
  }
}
