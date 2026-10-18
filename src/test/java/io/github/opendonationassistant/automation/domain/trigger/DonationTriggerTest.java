package io.github.opendonationassistant.automation.domain.trigger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.history.event.HistoryItemEvent;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class DonationTriggerTest {

  private final AutomationMetrics metrics = new AutomationMetrics(
    new SimpleMeterRegistry()
  );

  private DonationTrigger trigger() {
    return new DonationTrigger(new AutomationTriggerData("donation", Map.of()));
  }

  private Iteration iteration(Object source) {
    return new Iteration("recipient", source, List.of(), List.of(), metrics);
  }

  private HistoryItemEvent donation(
    @Nullable String nickname,
    @Nullable Amount amount,
    @Nullable String message
  ) {
    return new HistoryItemEvent(
      "id",
      "payment",
      "recipient-1",
      "ODA",
      "origin",
      Instant.EPOCH,
      nickname,
      amount,
      message,
      List.of(),
      List.of(),
      null
    );
  }

  @Test
  public void testTriggeredByHistoryItemEvent() {
    // Arrange
    var trigger = trigger();
    var event = donation("nickname", new Amount(100, 0, "RUB"), "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testNotTriggeredByUnrelatedSource() {
    // Arrange
    var trigger = trigger();

    // Act
    var triggered = trigger.isTriggered(new Object());

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testExtractsNicknameAmountAndMessageVariables() {
    // Arrange
    var trigger = trigger();
    var event = donation("nickname", new Amount(100, 0, "RUB"), "hello");
    var iteration = iteration(event);

    // Act
    trigger.extractVariables(event, iteration);

    // Assert
    assertEquals("nickname", iteration.variable("nickname").get().value());
    assertEquals("100 RUB", iteration.variable("amount").get().value());
    assertEquals("hello", iteration.variable("message").get().value());
  }

  @Test
  public void testSkipsVariablesWhenOptionalFieldsAreNull() {
    // Arrange
    var trigger = trigger();
    var event = donation(null, null, null);
    var iteration = iteration(event);

    // Act
    trigger.extractVariables(event, iteration);

    // Assert
    assertTrue(iteration.variable("nickname").isEmpty());
    assertTrue(iteration.variable("amount").isEmpty());
    assertTrue(iteration.variable("message").isEmpty());
  }

  @Test
  public void testRendersAmountWithoutCurrencyWhenCurrencyIsBlank() {
    // Arrange
    var trigger = trigger();
    var event = donation("nickname", new Amount(50, 0, ""), "hello");
    var iteration = iteration(event);

    // Act
    trigger.extractVariables(event, iteration);

    // Assert
    assertEquals("50", iteration.variable("amount").get().value());
  }

  @Test
  public void testExtractVariablesIgnoresUnrelatedSource() {
    // Arrange
    var trigger = trigger();
    var iteration = iteration(new Object());

    // Act
    trigger.extractVariables(new Object(), iteration);

    // Assert
    assertTrue(iteration.variable("nickname").isEmpty());
    assertTrue(iteration.variable("amount").isEmpty());
    assertTrue(iteration.variable("message").isEmpty());
  }
}
