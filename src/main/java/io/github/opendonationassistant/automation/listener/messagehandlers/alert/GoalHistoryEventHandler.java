package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.history.event.GoalHistoryEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;

@Singleton
public class GoalHistoryEventHandler
  extends AbstractMessageHandler<GoalHistoryEvent> {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public GoalHistoryEventHandler(ObjectMapper mapper, UIFacade ui) {
    this.ui = ui;
    super(mapper);
  }

  @Override
  public void handle(GoalHistoryEvent message) throws IOException {
    var variables = new ArrayList<Variable>();
    variables.add(
      new Variable(
        uuid.generate().toString(),
        "widgetId",
        message.widgetId(),
        "string"
      )
    );
    variables.add(
      new Variable(
        uuid.generate().toString(),
        "goalId",
        message.goalId(),
        "string"
      )
    );
    variables.add(
      new Variable(
        uuid.generate().toString(),
        "newAccumulatedAmount",
        message.amount(),
        "string"
      )
    );
    var event = new Event(uuid.generate().toString(), "GoalUpdate", variables);
    ui.sendEvent(message.recipientId(), event);
  }
}
