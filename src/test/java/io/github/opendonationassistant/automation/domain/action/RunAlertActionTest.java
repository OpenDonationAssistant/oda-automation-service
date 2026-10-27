package io.github.opendonationassistant.automation.domain.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.github.opendonationassistant.automation.EphemeralVariable;
import io.github.opendonationassistant.automation.RateLimiter;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class RunAlertActionTest {

  private final UIFacade ui = mock(UIFacade.class);
  private final AutomationMetrics metrics = new AutomationMetrics(
    new SimpleMeterRegistry()
  );
  private final RateLimiter rateLimiter = new RateLimiter();

  @Test
  public void testSendsAlertEventWithConfiguredAlertAndTriggerVariables() {
    var action = new RunAlertAction(
      new AutomationActionData(
        RunAlertAction.ID,
        Map.of("alertId", "alert-1")
      ),
      ui
    );
    var iteration = iterationWith("nickname", "100 RUB", "hello");

    action.execute(iteration);

    var captor = ArgumentCaptor.forClass(Event.class);
    verify(ui).sendEvent(eq("recipient"), captor.capture());
    var event = captor.getValue();
    assertEquals("Alert", event.type());
    assertEquals("alert-1", variable(event, "alertId"));
    assertEquals("nickname", variable(event, "nickname"));
    assertEquals("hello", variable(event, "message"));
    assertEquals("100", variable(event, "amount"));
    assertEquals(Boolean.TRUE, variable(event, "force"));
  }

  @Test
  public void testActionDataOverridesIterationVariables() {
    var action = new RunAlertAction(
      new AutomationActionData(
        RunAlertAction.ID,
        Map.of(
          "alertId",
          "alert-1",
          "nickname",
          "configured nickname",
          "message",
          "configured message",
          "system",
          "Twitch",
          "amount",
          "250",
          "count",
          5
        )
      ),
      ui
    );
    var iteration = iterationWith("nickname", "100 RUB", "hello");
    iteration.add(new EphemeralVariable<>("count", 2));

    action.execute(iteration);

    var captor = ArgumentCaptor.forClass(Event.class);
    verify(ui).sendEvent(eq("recipient"), captor.capture());
    var event = captor.getValue();
    assertEquals("configured nickname", variable(event, "nickname"));
    assertEquals("configured message", variable(event, "message"));
    assertEquals("Twitch", variable(event, "system"));
    assertEquals("250", variable(event, "amount"));
    assertEquals(5, variable(event, "count"));
  }

  @Test
  public void testFallsBackToIterationVariablesMissingInActionData() {
    var action = new RunAlertAction(
      new AutomationActionData(
        RunAlertAction.ID,
        Map.of("alertId", "alert-1", "nickname", "configured nickname")
      ),
      ui
    );

    action.execute(iterationWith("trigger nickname", "100 RUB", "hello"));

    var captor = ArgumentCaptor.forClass(Event.class);
    verify(ui).sendEvent(eq("recipient"), captor.capture());
    var event = captor.getValue();
    assertEquals("configured nickname", variable(event, "nickname"));
    assertEquals("hello", variable(event, "message"));
    assertEquals("100", variable(event, "amount"));
  }

  @Test
  public void testSkipsWhenAlertIdIsMissing() {
    var action = new RunAlertAction(
      new AutomationActionData(RunAlertAction.ID, Map.of()),
      ui
    );

    action.execute(iterationWith("nickname", "100 RUB", "hello"));

    verifyNoInteractions(ui);
  }

  private @Nullable Object variable(Event event, String name) {
    return event
      .variables()
      .stream()
      .filter(variable -> variable.name().equals(name))
      .map(Variable::value)
      .findFirst()
      .orElse(null);
  }

  private Iteration iterationWith(
    String nickname,
    String amount,
    String message
  ) {
    var iteration = new Iteration(
      "recipient",
      new Object(),
      List.of(),
      List.of(),
      metrics,
      rateLimiter
    );
    iteration.add(new EphemeralVariable<>("nickname", nickname));
    iteration.add(new EphemeralVariable<>("amount", amount));
    iteration.add(new EphemeralVariable<>("message", message));
    return iteration;
  }
}
