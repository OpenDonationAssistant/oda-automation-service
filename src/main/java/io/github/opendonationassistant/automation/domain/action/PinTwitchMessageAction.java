package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.twitch.SendAndPinChatMessageCommand;
import io.github.opendonationassistant.rabbit.RabbitClient;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PinTwitchMessageAction extends AutomationAction {

  private Logger log = LoggerFactory.getLogger(PinTwitchMessageAction.class);
  private final RabbitClient rabbitClient;

  public PinTwitchMessageAction(
    String id,
    Map<String, Object> value,
    RabbitClient rabbitClient
  ) {
    super(id, value);
    this.rabbitClient = rabbitClient;
  }

  public void execute() {
    final String recipientId = (String) getValue().get("recipientId");
    final String refreshTokenId = (String) getValue().get("refreshTokenId");
    final String message = (String) getValue().get("message");
    log.info(
      "Executing PinTwitchMessageAction, recipientId: {}, refreshTokenId: {}",
      recipientId,
      refreshTokenId
    );
    if (recipientId == null || refreshTokenId == null || message == null) {
      return;
    }
    rabbitClient.sendCommand(
      new SendAndPinChatMessageCommand(recipientId, refreshTokenId, message)
    );
  }
}
