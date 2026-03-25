package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.history.event.ReelResultHistoryEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;

@Singleton
public class ReelResultHistoryEventHandler
  extends AbstractMessageHandler<ReelResultHistoryEvent> {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public ReelResultHistoryEventHandler(ObjectMapper mapper, UIFacade ui) {
    this.ui = ui;
    super(mapper);
  }

  @Override
  public void handle(ReelResultHistoryEvent event) throws IOException {
    var variables = new ArrayList<Variable>();
    variables.add(
      new Variable(
        uuid.generate().toString(),
        "widgetId",
        event.widgetId(),
        "string"
      )
    );
    variables.add(
      new Variable(
        uuid.generate().toString(),
        "optionId",
        event.optionId(),
        "string"
      )
    );
    variables.add(
      new Variable(uuid.generate().toString(), "title", event.title(), "string")
    );
    ui.sendEvent(
      event.recipientId(),
      new Event(uuid.generate().toString(), "ReelResult", variables)
    );
  }
}
