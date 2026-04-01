package io.github.opendonationassistant.automation.listener.messagehandlers.twitch;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.twitch.events.TwitchChannelCheerEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Singleton
public class TwitchChannelCheerEventHandler
  extends AbstractMessageHandler<TwitchChannelCheerEvent> {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public TwitchChannelCheerEventHandler(UIFacade ui, ObjectMapper mapper) {
    super(mapper);
    this.ui = ui;
  }

  @Override
  public void handle(TwitchChannelCheerEvent received) throws IOException {
    var variables = new ArrayList<Variable>();
    variables.add(
      new Variable(
        uuid.generate().toString(),
        "message",
        received.message(),
        "string"
      )
    );
    variables.add(
      new Variable(
        uuid.generate().toString(),
        "bits",
        Integer.parseInt(received.bits()),
        "number"
      )
    );
    Optional.ofNullable(received.username()).ifPresent(username ->
      variables.add(
        new Variable(uuid.generate().toString(), "nickname", username, "string")
      )
    );
    ui.sendEvent(
      received.recipientId(),
      new Event(received.id(), "TwitchChannelCheerEvent", variables)
    );
  }
}
