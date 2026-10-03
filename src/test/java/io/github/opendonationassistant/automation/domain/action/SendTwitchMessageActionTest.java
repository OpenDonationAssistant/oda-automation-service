package io.github.opendonationassistant.automation.domain.action;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.action.SendTwitchMessageAction.SendTwitchMessageCommand;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.rabbit.RabbitClient;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SendTwitchMessageActionTest {

  private final RabbitClient rabbit = Mockito.mock(RabbitClient.class);
  private final Iteration iteration = Mockito.mock(Iteration.class);

  @Test
  public void testSendsCommandWhenFieldsPresent() {
    when(iteration.recipientId()).thenReturn("recipientId");
    var action = new SendTwitchMessageAction(
      new AutomationActionData(
        SendTwitchMessageAction.ID,
        Map.of(
          "senderRefreshTokenId",
          "sender",
          "recipientTwitchId",
          "channel",
          "message",
          "hello"
        )
      ),
      rabbit
    );

    action.execute(iteration);

    verify(rabbit).sendCommand(
      new SendTwitchMessageCommand("recipientId", "sender", "channel", "hello")
    );
  }

  @Test
  public void testDoesNotSendWhenMessageMissing() {
    when(iteration.recipientId()).thenReturn("recipientId");
    var action = new SendTwitchMessageAction(
      new AutomationActionData(
        SendTwitchMessageAction.ID,
        Map.of("senderRefreshTokenId", "sender", "recipientTwitchId", "channel")
      ),
      rabbit
    );

    action.execute(iteration);

    verifyNoInteractions(rabbit);
  }

  @Test
  public void testDoesNotSendWhenRecipientMissing() {
    when(iteration.recipientId()).thenReturn(null);
    var action = new SendTwitchMessageAction(
      new AutomationActionData(
        SendTwitchMessageAction.ID,
        Map.of(
          "senderRefreshTokenId",
          "sender",
          "recipientTwitchId",
          "channel",
          "message",
          "hello"
        )
      ),
      rabbit
    );

    action.execute(iteration);

    verifyNoInteractions(rabbit);
  }
}
