package io.github.opendonationassistant.automation.domain.trigger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class VariableEqualsValueTriggerTest {

  private static final String RECIPIENT_ID = "recipient-1";
  private static final String VARIABLE_ID = "variable-1";

  private VariableEqualsValueTrigger trigger(Map<String, Object> config) {
    return new VariableEqualsValueTrigger(
      new AutomationTriggerData(VariableEqualsValueTrigger.ID, config)
    );
  }

  private AutomationVariable.VariableCreated stringEvent(
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

  private AutomationVariable.VariableCreated numberEvent(
    String id,
    String name,
    String value
  ) {
    return new AutomationVariable.VariableCreated(
      id,
      RECIPIENT_ID,
      name,
      "number",
      value
    );
  }

  private AutomationVariable.VariableUpdated stringUpdate(
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

  private AutomationVariable.VariableUpdated numberUpdate(
    String id,
    String name,
    String value
  ) {
    return new AutomationVariable.VariableUpdated(
      id,
      RECIPIENT_ID,
      name,
      "number",
      value
    );
  }

  @Test
  public void testTriggeredWhenStringValueEqualsExpected() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "greeting", "value", "hello")
    );
    var event = stringEvent(VARIABLE_ID, "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testNotTriggeredWhenStringValueDiffers() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "greeting", "value", "hello")
    );
    var event = stringEvent(VARIABLE_ID, "greeting", "bye");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testComparesNumbersNumerically() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "counter", "value", "10.0")
    );
    var event = numberEvent(VARIABLE_ID, "counter", "10");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testMatchesByIdWhenNameDiffers() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "old name", "value", "hello")
    );
    var event = stringEvent(VARIABLE_ID, "renamed", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testUpdatedTriggeredWhenStringValueEqualsExpected() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "greeting", "value", "hello")
    );
    var event = stringUpdate(VARIABLE_ID, "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testUpdatedNotTriggeredWhenStringValueDiffers() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "greeting", "value", "hello")
    );
    var event = stringUpdate(VARIABLE_ID, "greeting", "bye");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testUpdatedComparesNumbersNumerically() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "counter", "value", "10.0")
    );
    var event = numberUpdate(VARIABLE_ID, "counter", "10");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testUpdatedMatchesByIdWhenNameDiffers() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "old name", "value", "hello")
    );
    var event = stringUpdate(VARIABLE_ID, "renamed", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertTrue(triggered);
  }

  @Test
  public void testUpdatedNotTriggeredForDifferentVariable() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "greeting", "value", "hello")
    );
    var event = stringUpdate("other-id", "other-name", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testUpdatedNotTriggeredWhenExpectedValueMissing() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "greeting"));
    var event = stringUpdate(VARIABLE_ID, "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testUpdatedNotTriggeredWhenVariableNotConfigured() {
    // Arrange
    var trigger = trigger(Map.of("value", "hello"));
    var event = stringUpdate(VARIABLE_ID, "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testNotTriggeredForDifferentVariable() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "greeting", "value", "hello")
    );
    var event = stringEvent("other-id", "other-name", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testNotTriggeredWhenExpectedValueMissing() {
    // Arrange
    var trigger = trigger(Map.of("id", VARIABLE_ID, "name", "greeting"));
    var event = stringEvent(VARIABLE_ID, "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testNotTriggeredWhenVariableNotConfigured() {
    // Arrange
    var trigger = trigger(Map.of("value", "hello"));
    var event = stringEvent(VARIABLE_ID, "greeting", "hello");

    // Act
    var triggered = trigger.isTriggered(event);

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testNotTriggeredForNonVariableSource() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "greeting", "value", "hello")
    );

    // Act
    var triggered = trigger.isTriggered(new Object());

    // Assert
    assertFalse(triggered);
  }

  @Test
  public void testNotTriggeredForDeletedVariable() {
    // Arrange
    var trigger = trigger(
      Map.of("id", VARIABLE_ID, "name", "greeting", "value", "hello")
    );
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

}
