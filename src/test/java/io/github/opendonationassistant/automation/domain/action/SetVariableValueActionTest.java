package io.github.opendonationassistant.automation.domain.action;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.http.client.HttpClient;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class SetVariableValueActionTest {

  private static final String RECIPIENT_ID = "recipient-1";
  private static final String VARIABLE_ID = "variable-1";

  private final AutomationVariableRepository variables = mock(
    AutomationVariableRepository.class
  );
  private final AutomationVariableDataRepository dataRepository = mock(
    AutomationVariableDataRepository.class
  );
  private final Iteration iteration = mock(Iteration.class);
  private final RabbitClient rabbit = mock(RabbitClient.class);
  private final HttpClient httpClient = mock(HttpClient.class);
  private final UIFacade uiFacade = mock(UIFacade.class);

  private SetVariableValueAction action(Map<String, Object> value) {
    return new SetVariableValueAction(
      new AutomationActionData(SetVariableValueAction.ID, value),
      RECIPIENT_ID,
      variables
    );
  }

  private AutomationStringVariable stringVariable(String value) {
    return new AutomationStringVariable(
      new AutomationVariableData(
        VARIABLE_ID,
        "string",
        "greeting",
        RECIPIENT_ID,
        value
      ),
      dataRepository,
      rabbit
    );
  }

  private AutomationNumberVariable numberVariable(String value) {
    return new AutomationNumberVariable(
      new AutomationVariableData(
        VARIABLE_ID,
        "number",
        "counter",
        RECIPIENT_ID,
        value
      ),
      dataRepository,
      rabbit
    );
  }

  @Test
  public void testSetsStringVariableValue() {
    var variable = stringVariable("hello");
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    action(Map.of("id", VARIABLE_ID, "value", "world")).execute(iteration);

    assertEquals("world", variable.value());
    assertEquals("greeting", variable.name());
  }

  @Test
  public void testSetsStringVariableToEmptyString() {
    var variable = stringVariable("hello");
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    action(Map.of("id", VARIABLE_ID, "value", "")).execute(iteration);

    assertEquals("", variable.value());
  }

  @Test
  public void testSetsNumberVariableFromString() {
    var variable = numberVariable("1");
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    action(Map.of("id", VARIABLE_ID, "value", "10.5")).execute(iteration);

    assertEquals(0, new BigDecimal("10.5").compareTo(variable.value()));
  }

  @Test
  public void testAcceptsNumericPayload() {
    var variable = numberVariable("1");
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    action(Map.of("id", VARIABLE_ID, "value", 42)).execute(iteration);

    assertEquals(0, new BigDecimal("42").compareTo(variable.value()));
  }

  @Test
  public void testInvalidNumberLeavesValueUnchanged() {
    var variable = numberVariable("42");
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    assertDoesNotThrow(() ->
      action(Map.of("id", VARIABLE_ID, "value", "abc")).execute(iteration)
    );

    assertEquals(0, new BigDecimal("42").compareTo(variable.value()));
    verifyNoInteractions(dataRepository);
  }

  @Test
  public void testMissingValueLeavesValueUnchanged() {
    var variable = stringVariable("hello");
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    action(Map.of("id", VARIABLE_ID)).execute(iteration);

    assertEquals("hello", variable.value());
    verifyNoInteractions(dataRepository);
  }

  @Test
  public void testDoesNothingWhenVariableNotFound() {
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.empty()
    );

    assertDoesNotThrow(() ->
      action(Map.of("id", VARIABLE_ID, "value", "world")).execute(iteration)
    );

    verifyNoInteractions(dataRepository);
  }

  @Test
  public void testExposesConfiguredValue() {
    assertEquals(
      Optional.of("world"),
      action(Map.of("id", VARIABLE_ID, "value", "world")).getValue()
    );
    assertTrue(action(Map.of("id", VARIABLE_ID)).getValue().isEmpty());
  }

  @Test
  public void testFactoryCreatesAction() {
    var factory = new ActionFactory(variables, rabbit, httpClient, uiFacade);

    AutomationAction created = factory.from(
      RECIPIENT_ID,
      new AutomationActionData(
        SetVariableValueAction.ID,
        Map.of("id", VARIABLE_ID, "value", "world")
      )
    );

    assertInstanceOf(SetVariableValueAction.class, created);
  }
}
