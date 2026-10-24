package io.github.opendonationassistant.automation.listener.messagehandlers.twitch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.events.twitch.events.TwitchUserBannedEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@MicronautTest(environments = "allinone")
public class TwitchUserBannedEventHandlerTest {

  @Inject
  TwitchUserBannedEventHandler registeredHandler;

  @Inject
  ObjectMapper objectMapper;

  private UIFacade uiFacade = mock(UIFacade.class);
  private TwitchUserBannedEventHandler handler =
    new TwitchUserBannedEventHandler(uiFacade, objectMapper);

  @Test
  void testHandleBannedEvent() throws IOException {
    var event = new TwitchUserBannedEvent(
      "test-event-id",
      "recipient-123",
      "banned-user",
      true
    );

    handler.handle(event);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(uiFacade).sendEvent(eq("recipient-123"), eventCaptor.capture());

    Event capturedEvent = eventCaptor.getValue();
    assertEquals("test-event-id", capturedEvent.id());
    assertEquals("TwitchUserBannedEvent", capturedEvent.type());
    assertEquals(1, capturedEvent.variables().size());

    var nickname = capturedEvent.variables().get(0);
    assertEquals("nickname", nickname.name());
    assertEquals("banned-user", nickname.value());
    assertEquals("string", nickname.type());
    assertNotNull(nickname.id());
    assertFalse(nickname.id().isEmpty());
  }
}
