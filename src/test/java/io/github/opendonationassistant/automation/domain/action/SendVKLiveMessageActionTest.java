package io.github.opendonationassistant.automation.domain.action;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.action.SendVKLiveMessageAction.SendVKLiveMessageCommand;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.rabbit.RabbitClient;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SendVKLiveMessageActionTest {

  private final RabbitClient rabbit = Mockito.mock(RabbitClient.class);
  private final Iteration iteration = Mockito.mock(Iteration.class);

  @Test
  public void testSendsCommandWhenFieldsPresent() {
    when(iteration.recipientId()).thenReturn("recipientId");
    var action = new SendVKLiveMessageAction(
      new AutomationActionData(
        SendVKLiveMessageAction.ID,
        Map.of(
          "senderRefreshTokenId",
          "sender",
          "recipientVkId",
          "channel",
          "message",
          "hello"
        )
      ),
      rabbit
    );

    action.execute(iteration);

    verify(rabbit).sendCommand(
      new SendVKLiveMessageCommand("recipientId", "sender", "channel", "hello")
    );
  }

  @Test
  public void testDoesNotSendWhenFieldsMissing() {
    when(iteration.recipientId()).thenReturn("recipientId");
    var action = new SendVKLiveMessageAction(
      new AutomationActionData(SendVKLiveMessageAction.ID, Map.of()),
      rabbit
    );

    action.execute(iteration);

    verifyNoInteractions(rabbit);
  }
}
