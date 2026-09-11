package io.github.opendonationassistant.automation.domain.action;

import static org.mockito.Mockito.verify;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.action.TwitchAnnounceAction.TwitchAnnounceCommand;
import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.events.twitch.events.TwitchStreamStartedEvent;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TwitchAnnounceActionTest {

  RabbitClient rabbit = Mockito.mock(RabbitClient.class);
  AutomationMetrics metrics = new AutomationMetrics(new SimpleMeterRegistry());

  @Test
  public void testSendsAnnouncementCommandWhenExecuted() {
    var actionData = new AutomationActionData(
      "twitch-announce",
      Map.of(
        "senderRefreshTokenId",
        "senderRefreshTokenId",
        "recipientTwitchId",
        "recipientTwitchId",
        "moderatorTwitchId",
        "moderatorTwitchId",
        "message",
        "message",
        "color",
        "purple"
      )
    );
    var action = new TwitchAnnounceAction(actionData, rabbit);
    var iteration = new Iteration(
      "testuser",
      new TwitchStreamStartedEvent("eventId", "testuser", "url"),
      List.of(),
      List.of(),
      metrics
    );

    action.execute(iteration);

    verify(rabbit).sendCommand(
      new TwitchAnnounceCommand(
        "testuser",
        "senderRefreshTokenId",
        "recipientTwitchId",
        "moderatorTwitchId",
        "message",
        "purple"
      )
    );
  }

  @Test
  public void testDoesNotSendCommandWhenRequiredFieldsMissing() {
    var actionData = new AutomationActionData(
      "twitch-announce",
      Map.of("message", "message")
    );
    var action = new TwitchAnnounceAction(actionData, rabbit);
    var iteration = new Iteration(
      "testuser",
      new TwitchStreamStartedEvent("eventId", "testuser", "url"),
      List.of(),
      List.of(),
      metrics
    );

    action.execute(iteration);

    Mockito.verifyNoInteractions(rabbit);
  }
}