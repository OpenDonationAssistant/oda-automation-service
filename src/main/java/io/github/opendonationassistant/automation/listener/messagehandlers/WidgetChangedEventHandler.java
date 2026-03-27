package io.github.opendonationassistant.automation.listener.messagehandlers;

import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.widget.WidgetChangedEvent;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;

@Singleton
public class WidgetChangedEventHandler
  extends AbstractMessageHandler<WidgetChangedEvent> {

  private final UIFacade ui;

  @Inject
  public WidgetChangedEventHandler(ObjectMapper mapper, UIFacade ui) {
    super(mapper);
    this.ui = ui;
  }

  @Override
  public void handle(WidgetChangedEvent event) throws IOException {
    ui.reload(event.widget().ownerId(), event.widget().id());
  }
}
