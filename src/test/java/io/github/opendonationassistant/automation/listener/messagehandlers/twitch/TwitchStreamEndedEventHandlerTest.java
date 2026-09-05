package io.github.opendonationassistant.automation.listener.messagehandlers.twitch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.events.twitch.events.TwitchStreamEndedEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@MicronautTest(environments = "allinone")
public class TwitchStreamEndedEventHandlerTest {

  @Inject
  ObjectMapper objectMapper;

  private UIFacade uiFacade = mock(UIFacade.class);
  private TwitchStreamEndedEventHandler handler =
    new TwitchStreamEndedEventHandler(uiFacade, objectMapper);

  @Test
  void testHandleStreamEndedEvent() throws IOException {
    var event = new TwitchStreamEndedEvent("test-event-id", "recipient-123");

    handler.handle(event);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(uiFacade).sendEvent(eq("recipient-123"), eventCaptor.capture());

    Event capturedEvent = eventCaptor.getValue();
    assertEquals("test-event-id", capturedEvent.id());
    assertEquals("TwitchStreamEndedEvent", capturedEvent.type());
    assertEquals(0, capturedEvent.variables().size());
  }
}