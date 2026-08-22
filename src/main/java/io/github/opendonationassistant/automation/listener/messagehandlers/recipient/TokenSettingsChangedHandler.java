package io.github.opendonationassistant.automation.listener.messagehandlers.recipient;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.HasRecipientId;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Singleton
public class TokenSettingsChangedHandler
  extends AbstractMessageHandler<
    TokenSettingsChangedHandler.TokenSettingsChanged
  > {

  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  @Inject
  public TokenSettingsChangedHandler(ObjectMapper mapper, UIFacade ui) {
    super(mapper);
    this.ui = ui;
  }

  @Serdeable
  public static record TokenSettingsChanged(
    String id,
    String type,
    String recipientId,
    String system,
    boolean enabled,
    boolean deleted,
    Map<String, Object> settings,
    TokenEvent event
  )
    implements HasRecipientId {}

  @Serdeable
  public static enum TokenEvent {
    TOKEN_CREATED,
    TOKEN_UPDATED,
    SETTINGS_CHANGED,
    TOKEN_TOGGLED,
    TOKEN_DELETED,
  }

  @Override
  public void handle(TokenSettingsChanged message) throws IOException {
    var event = new Event(
      message.id(),
      "AuthUpdated",
      List.of(
        new Variable(
          uuid.generate().toString(),
          "tokenId",
          message.id(),
          "string"
        ),
        new Variable(
          uuid.generate().toString(),
          "event",
          message.event().name(),
          "string"
        ),
        new Variable(
          uuid.generate().toString(),
          "tokenType",
          message.type(),
          "string"
        )
      )
    );
    ui.sendEvent(message.recipientId(), event);
  }
}
