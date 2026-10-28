package io.github.opendonationassistant.automation.domain.trigger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.RateLimiter;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class VariableValueChangedTriggerTest {

  private static final String RECIPIENT_ID = "recipient-1";
  private static final String VARIABLE_ID = "variable-1";

  private VariableValueChangedTrigger trigger(Map<String, Object> config) {
    return new VariableValueChangedTrigger(
      new AutomationTriggerData(VariableValueChangedTrigger.ID, config)
    );
  }

  private AutomationVariable.VariableCreated createdEvent(
    String id,
    String name,
    String value
  ) {
    return new AutomationVariable.VariableCreated(
      id,
      RECIPIENT_ID,
      name,
      "string",
      value
    );
  }

  private AutomationVariable.VariableUpdated updatedEvent(
    String id,
    String name,
    String value
  ) {
    return new AutomationVariable.VariableUpdated(
      id,
      RECIPIENT_ID,
      name,
      "string",
      value
    );
  }

  private Iteration iteration() {
    return new Iteration(
      RECIPIENT_ID,
      new Object(),
      List.of(),
      List.of(),
      new AutomationMetrics(new SimpleMeterRegistry()),
      new RateLimiter()
    );
  }

  @Test
  public void testTriggeredOnCreatedForMatchingId() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "greeting"));
    var event = createdEvent(VARIABLE_ID, "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testTriggeredOnCreatedForAnyValue() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID));

    // Act
    var triggered = trigger.isTriggered(
      createdEvent(VARIABLE_ID, "greeting", "any value")
    );

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testTriggeredOnUpdatedForMatchingId() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "greeting"));
    var event = updatedEvent(VARIABLE_ID, "greeting", "bye");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testTriggeredOnUpdatedForMatchingIdWhenNameDiffers() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "old name"));
    var event = updatedEvent(VARIABLE_ID, "renamed", "bye");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testMatchesByNameWhenIdAbsent() {
    // Arrange
    var trigger = trigger(Map.of("name", "greeting"));
    var event = updatedEvent("other-id", "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testNotTriggeredForDifferentVariableId() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "greeting"));
    var event = updatedEvent("other-id", "other-name", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testNotTriggeredWhenNeitherIdNorNameConfigured() {
    // Arrange
    var trigger = trigger(Map.of());
    var event = createdEvent(VARIABLE_ID, "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testNotTriggeredWhenConfiguredValuesAreBlank() {
    // Arrange
    var trigger = trigger(Map.of("id", " ", "name", " "));
    var event = createdEvent(VARIABLE_ID, "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testNotTriggeredForDeletedVariable() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "greeting"));
    var event = new AutomationVariable.VariableDeleted(
      VARIABLE_ID,
      RECIPIENT_ID,
      "greeting",
      "string",
      "hello"
    );

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testNotTriggeredForNonVariableSource() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "greeting"));

    // Act
    var triggered = trigger.isTriggered(new Object());

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testExtractVariablesAddsValueAndVariableName() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "greeting"));
    var iteration = iteration();
    var event = updatedEvent(VARIABLE_ID, "greeting", "hello");

    // Act
    trigger.extractVariables(event, iteration);

    // Assert
    assertEquals(
      Optional.of("hello"),
      iteration.resolveVariable("value", Map.of())
    );
    assertEquals(
      Optional.of("greeting"),
      iteration.resolveVariable("variableName", Map.of())
    );
  }

  @Test
  public void testExtractVariablesIgnoresNonVariableSource() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "greeting"));
    var iteration = iteration();

    // Act
    trigger.extractVariables(new Object(), iteration);

    // Assert
    assertTrue(iteration.resolveVariable("value", Map.of()).isEmpty());
    assertTrue(iteration.resolveVariable("variableName", Map.of()).isEmpty());
  }
}
