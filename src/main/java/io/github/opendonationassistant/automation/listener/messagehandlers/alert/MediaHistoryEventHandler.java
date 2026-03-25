package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.history.event.MediaHistoryEvent;
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
public class MediaHistoryEventHandler
  extends AbstractMessageHandler<MediaHistoryEvent> {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public MediaHistoryEventHandler(ObjectMapper mapper, UIFacade ui) {
    this.ui = ui;
    super(mapper);
  }

  @Override
  public void handle(MediaHistoryEvent event) throws IOException {
    var variables = new ArrayList<Variable>();
    variables.add(
      new Variable(uuid.generate().toString(), "url", event.url(), "string")
    );
    variables.add(
      new Variable(uuid.generate().toString(), "title", event.title(), "string")
    );
    variables.add(
      new Variable(
        uuid.generate().toString(),
        "thumbnail",
        event.thumbnail(),
        "string"
      )
    );
    variables.add(
      new Variable(
        uuid.generate().toString(),
        "mediaId",
        event.mediaId(),
        "string"
      )
    );
    Optional.ofNullable(event.source()).ifPresent(source ->
      variables.add(
        new Variable(uuid.generate().toString(), "source", source, "string")
      )
    );
    Optional.ofNullable(event.source()).ifPresent(originId ->
      variables.add(
        new Variable(
          uuid.generate().toString(),
          "originId",
          originId,
          "string"
        )
      )
    );
    ui.sendEvent(
      event.recipientId(),
      new Event(uuid.generate().toString(), "MediaRequested", variables)
    );
  }
}
