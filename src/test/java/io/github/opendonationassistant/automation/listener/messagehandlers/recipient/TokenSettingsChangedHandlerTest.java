package io.github.opendonationassistant.automation.listener.messagehandlers.recipient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class TokenSettingsChangedHandlerTest {

  UIFacade ui = mock(UIFacade.class);
  TokenSettingsChangedHandler handler = new TokenSettingsChangedHandler(
    mock(ObjectMapper.class),
    ui
  );

  @Test
  public void testType() {
    assertEquals("TokenSettingsChanged", handler.type());
  }

  @Test
  public void testConvertsToAuthUpdatedEvent() throws IOException {
    var message = new TokenSettingsChangedHandler.TokenSettingsChanged(
      "token-1",
      "refreshToken",
      "recipient-1",
      "Twitch",
      true,
      false,
      Map.of("channel", "test")
    );

    handler.handle(message);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(ui).sendEvent(eq("recipient-1"), eventCaptor.capture());

    Event capturedEvent = eventCaptor.getValue();
    assertEquals("token-1", capturedEvent.id());
    assertEquals("AuthUpdated", capturedEvent.type());
    assertEquals(1, capturedEvent.variables().size());

    var tokenIdVar = capturedEvent.variables().get(0);
    assertEquals("tokenId", tokenIdVar.name());
    assertEquals("token-1", tokenIdVar.value());
    assertEquals("string", tokenIdVar.type());
    assertNotNull(tokenIdVar.id());
    assertFalse(tokenIdVar.id().isEmpty());
  }
}
