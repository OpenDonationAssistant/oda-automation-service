package io.github.opendonationassistant.automation.listener.messagehandlers.twitch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.events.twitch.events.TwitchStreamStartedEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@MicronautTest(environments = "allinone")
public class TwitchStreamStartedEventHandlerTest {

  @Inject
  ObjectMapper objectMapper;

  private UIFacade uiFacade = mock(UIFacade.class);
  private TwitchStreamStartedEventHandler handler =
    new TwitchStreamStartedEventHandler(uiFacade, objectMapper);

  @Test
  void testHandleStreamStartedEvent() throws IOException {
    var event = new TwitchStreamStartedEvent(
      "test-event-id",
      "recipient-123",
      "https://example.com/thumbnail.jpg"
    );

    handler.handle(event);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(uiFacade).sendEvent(eq("recipient-123"), eventCaptor.capture());

    Event capturedEvent = eventCaptor.getValue();
    assertEquals("test-event-id", capturedEvent.id());
    assertEquals("TwitchStreamStartedEvent", capturedEvent.type());
    assertEquals(1, capturedEvent.variables().size());

    var thumbnailVar = capturedEvent.variables().get(0);
    assertEquals("thumbnailUrl", thumbnailVar.name());
    assertEquals("https://example.com/thumbnail.jpg", thumbnailVar.value());
    assertEquals("string", thumbnailVar.type());
    assertNotNull(thumbnailVar.id());
    assertFalse(thumbnailVar.id().isEmpty());
  }
}