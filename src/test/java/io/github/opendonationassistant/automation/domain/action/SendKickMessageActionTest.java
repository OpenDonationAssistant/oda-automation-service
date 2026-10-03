package io.github.opendonationassistant.automation.domain.action;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.action.SendKickMessageAction.SendKickMessageCommand;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.rabbit.RabbitClient;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SendKickMessageActionTest {

  private final RabbitClient rabbit = Mockito.mock(RabbitClient.class);
  private final Iteration iteration = Mockito.mock(Iteration.class);

  @Test
  public void testSendsCommandWhenFieldsPresent() {
    when(iteration.recipientId()).thenReturn("recipientId");
    var action = new SendKickMessageAction(
      new AutomationActionData(
        SendKickMessageAction.ID,
        Map.of(
          "senderRefreshTokenId",
          "sender",
          "recipientKickId",
          "channel",
          "message",
          "hello"
        )
      ),
      rabbit
    );

    action.execute(iteration);

    verify(rabbit).sendCommand(
      new SendKickMessageCommand("recipientId", "sender", "channel", "hello")
    );
  }

  @Test
  public void testDoesNotSendWhenFieldsMissing() {
    when(iteration.recipientId()).thenReturn("recipientId");
    var action = new SendKickMessageAction(
      new AutomationActionData(SendKickMessageAction.ID, Map.of()),
      rabbit
    );

    action.execute(iteration);

    verifyNoInteractions(rabbit);
  }
}
