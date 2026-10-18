package io.github.opendonationassistant.automation.domain.action;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.EphemeralVariable;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.action.SendWebhookMessageAction.SendDiscordWebhookMessageCommand;
import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link SendWebhookMessageAction}.
 *
 * <p>Objective: a rule action must resolve its {@code message} template through
 * {@link VariableResolver} using the variables available on the current
 * {@link Iteration}, and emit exactly one
 * {@link SendDiscordWebhookMessageCommand} through the {@link RabbitClient}.
 *
 * <p>Guard rails under test: no command may be emitted when the iteration has no
 * recipient id, when the action carries no {@code message} value, or when the
 * resolved message is blank.
 */
public class SendWebhookMessageActionTest {

  // Shared test doubles: RabbitClient is the only external dependency and is
  // always mocked so the tests stay deterministic (no network, no broker).
  private final RabbitClient rabbit = Mockito.mock(RabbitClient.class);
  private final AutomationMetrics metrics = new AutomationMetrics(
    new SimpleMeterRegistry()
  );

  /**
   * Positive case: every {@code <placeholder>} in the template is replaced with
   * the matching ephemeral variable value and the fully resolved message is sent
   * to the iteration's recipient.
   */
  @Test
  public void testSendsCommandWithResolvedTemplateWhenExecuted() {
    // Arrange
    var iteration = iterationWith(
      new EphemeralVariable<>("nickname", "alice"),
      new EphemeralVariable<>("amount", "100 RUB"),
      new EphemeralVariable<>("message", "hi")
    );
    var action = new SendWebhookMessageAction(
      new AutomationActionData(
        SendWebhookMessageAction.ID,
        Map.of(
          "message",
          "<nickname> задонатил <amount> со словами: <message>"
        )
      ),
      rabbit
    );

    // Act
    action.execute(iteration);

    // Assert
    verify(rabbit).sendCommand(
      new SendDiscordWebhookMessageCommand(
        "recipientId",
        "alice задонатил 100 RUB со словами: hi"
      )
    );
  }

  /**
   * Negative case: when the iteration has no recipient id there is nobody to
   * send to, so the client must never be touched.
   */
  @Test
  public void testDoesNotSendCommandWhenRecipientIdIsNull() {
    // Arrange
    var iteration = Mockito.mock(Iteration.class);
    when(iteration.recipientId()).thenReturn(null);
    var action = new SendWebhookMessageAction(
      new AutomationActionData(
        SendWebhookMessageAction.ID,
        Map.of("message", "hello")
      ),
      rabbit
    );

    // Act
    action.execute(iteration);

    // Assert
    verifyNoInteractions(rabbit);
  }

  /**
   * Negative case: when the action data has no {@code message} key the template
   * is empty, so no command may be sent even though the recipient is present.
   */
  @Test
  public void testDoesNotSendCommandWhenMessageKeyMissing() {
    // Arrange
    var iteration = iterationWith(
      new EphemeralVariable<>("nickname", "alice")
    );
    var action = new SendWebhookMessageAction(
      new AutomationActionData(SendWebhookMessageAction.ID, Map.of()),
      rabbit
    );

    // Act
    action.execute(iteration);

    // Assert
    verifyNoInteractions(rabbit);
  }

  /**
   * Negative case: a template that only contains whitespace resolves to a blank
   * message and must be suppressed.
   */
  @Test
  public void testDoesNotSendCommandWhenResolvedMessageIsBlank() {
    // Arrange
    var iteration = iterationWith(
      new EphemeralVariable<>("nickname", "alice")
    );
    var action = new SendWebhookMessageAction(
      new AutomationActionData(
        SendWebhookMessageAction.ID,
        Map.of("message", "   ")
      ),
      rabbit
    );

    // Act
    action.execute(iteration);

    // Assert
    verifyNoInteractions(rabbit);
  }

  /**
   * Builds a real (non-mocked) {@link Iteration} with a fixed recipient id and
   * the supplied ephemeral variables, mirroring how the engine seeds variables.
   */
  private Iteration iterationWith(EphemeralVariable<?>... variables) {
    var iteration = new Iteration(
      "recipientId",
      new Object(),
      List.of(),
      List.of(),
      metrics
    );
    for (var variable : variables) {
      iteration.add(variable);
    }
    return iteration;
  }
}
