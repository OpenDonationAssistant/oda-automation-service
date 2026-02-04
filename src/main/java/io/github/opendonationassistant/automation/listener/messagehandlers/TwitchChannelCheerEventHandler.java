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
import java.util.ArrayList;
import java.util.Optional;

@Singleton
public class TwitchChannelCheerEventHandler implements MessageHandler {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public TwitchChannelCheerEventHandler(UIFacade ui) {
    this.ui = ui;
  }

  @Override
  public String type() {
    return "TwitchChannelCheerEvent";
  }

  @Override
  public void handle(byte[] message) throws IOException {
    var received = ObjectMapper.getDefault()
      .readValue(
        message,
        io.github.opendonationassistant.events.twitch.events.TwitchChannelCheerEvent.class
      );
    if (received == null) {
      return;
    }
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
    var event = new io.github.opendonationassistant.events.UIFacade.Event(
      received.id(),
      "TwitchChannelCheerEvent",
      variables
    );
    ui.sendEvent(received.recipientId(), event);
  }
}
