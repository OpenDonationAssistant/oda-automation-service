package io.github.opendonationassistant.automation.listener.messagehandlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.events.twitch.events.TwitchChannelCheerEvent;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@MicronautTest(environments = "allinone")
public class TwitchChannelCheerEventHandlerTest {

  @Inject
  ObjectMapper objectMapper;

  private UIFacade uiFacade = mock(UIFacade.class);
  private TwitchChannelCheerEventHandler handler =
    new TwitchChannelCheerEventHandler(uiFacade, objectMapper);

  @Test
  void testHandleCheerEventWithUsername() throws IOException {
    var event = new TwitchChannelCheerEvent(
      "test-event-id",
      "recipient-123",
      "testuser",
      "Test cheer message",
      "100"
    );

    handler.handle(event);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(uiFacade).sendEvent(eq("recipient-123"), eventCaptor.capture());

    Event capturedEvent = eventCaptor.getValue();
    assertEquals("test-event-id", capturedEvent.id());
    assertEquals("TwitchChannelCheerEvent", capturedEvent.type());
    assertEquals(3, capturedEvent.variables().size());

    var messageVar = capturedEvent.variables().get(0);
    assertEquals("message", messageVar.name());
    assertEquals("Test cheer message", messageVar.value());
    assertEquals("string", messageVar.type());
    assertNotNull(messageVar.id());
    assertFalse(messageVar.id().isEmpty());

    var bitsVar = capturedEvent.variables().get(1);
    assertEquals("bits", bitsVar.name());
    assertEquals(100, bitsVar.value());
    assertEquals("number", bitsVar.type());
    assertNotNull(bitsVar.id());
    assertFalse(bitsVar.id().isEmpty());

    var nicknameVar = capturedEvent.variables().get(2);
    assertEquals("nickname", nicknameVar.name());
    assertEquals("testuser", nicknameVar.value());
    assertEquals("string", nicknameVar.type());
    assertNotNull(nicknameVar.id());
    assertFalse(nicknameVar.id().isEmpty());
  }

  @Test
  void testHandleCheerEventWithoutUsername() throws IOException {
    var event = new TwitchChannelCheerEvent(
      "test-event-id-2",
      "recipient-456",
      null,
      "Another cheer",
      "50"
    );

    handler.handle(event);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(uiFacade).sendEvent(eq("recipient-456"), eventCaptor.capture());

    Event capturedEvent = eventCaptor.getValue();
    assertEquals("test-event-id-2", capturedEvent.id());
    assertEquals("TwitchChannelCheerEvent", capturedEvent.type());
    assertEquals(2, capturedEvent.variables().size());

    for (var variable : capturedEvent.variables()) {
      assertNotNull(variable.id());
      assertFalse(variable.id().isEmpty());
    }
  }
}
