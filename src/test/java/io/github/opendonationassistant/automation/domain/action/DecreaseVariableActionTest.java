package io.github.opendonationassistant.automation.domain.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class DecreaseVariableActionTest {

  private static final String RECIPIENT_ID = "recipient-1";
  private static final String VARIABLE_ID = "variable-1";

  private final AutomationVariableRepository variables = mock(
    AutomationVariableRepository.class
  );
  private final AutomationVariableDataRepository dataRepository = mock(
    AutomationVariableDataRepository.class
  );
  private final Iteration iteration = mock(Iteration.class);

  private AutomationNumberVariable numberVariable(String value) {
    return new AutomationNumberVariable(
      new AutomationVariableData(
        VARIABLE_ID,
        "number",
        "counter",
        RECIPIENT_ID,
        value
      ),
      dataRepository
    );
  }

  private DecreaseVariableAction action(Map<String, Object> value) {
    return new DecreaseVariableAction(
      new AutomationActionData(DecreaseVariableAction.ID, value),
      RECIPIENT_ID,
      variables
    );
  }

  @Test
  public void testDecreasesVariableValueByConfiguredAmount() {
    var variable = numberVariable("10");
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    action(Map.of("id", VARIABLE_ID, "value", 3)).execute(iteration);

    assertEquals(new BigDecimal("7"), variable.value());
  }

  @Test
  public void testDefaultsToNoChangeWhenAmountMissing() {
    var variable = numberVariable("10");
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    action(Map.of("id", VARIABLE_ID)).execute(iteration);

    assertEquals(new BigDecimal("10"), variable.value());
  }

  @Test
  public void testDoesNothingWhenVariableNotFound() {
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.empty()
    );

    action(Map.of("id", VARIABLE_ID, "value", 3)).execute(iteration);

    verifyNoInteractions(dataRepository);
  }

  @Test
  public void testDoesNothingWhenVariableIsNotNumeric() {
    var variable = new AutomationStringVariable(
      new AutomationVariableData(
        VARIABLE_ID,
        "string",
        "greeting",
        RECIPIENT_ID,
        "hello"
      ),
      dataRepository
    );
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    action(Map.of("id", VARIABLE_ID, "value", 3)).execute(iteration);

    assertEquals("hello", variable.value());
    verifyNoInteractions(dataRepository);
  }

  @Test
  public void testReadsVariableIdAndAmountFromActionData() {
    var action = action(Map.of("id", VARIABLE_ID, "value", 5));

    assertEquals(Optional.of(VARIABLE_ID), action.getVariableId());
    assertEquals(Optional.of(5), action.getAmount());
  }

  @Test
  public void testDefaultsToEmptyAndZeroWhenUnset() {
    var action = action(Map.of());

    assertTrue(action.getVariableId().isEmpty());
    assertTrue(action.getAmount().isEmpty());
  }
}
