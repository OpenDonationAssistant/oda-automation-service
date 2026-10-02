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

public class ResetVariableActionTest {

  private static final String RECIPIENT_ID = "recipient-1";
  private static final String VARIABLE_ID = "variable-1";

  private final AutomationVariableRepository variables = mock(
    AutomationVariableRepository.class
  );
  private final AutomationVariableDataRepository dataRepository = mock(
    AutomationVariableDataRepository.class
  );
  private final Iteration iteration = mock(Iteration.class);

  private ResetVariableAction action() {
    return new ResetVariableAction(
      new AutomationActionData(ResetVariableAction.ID, Map.of("id", VARIABLE_ID)),
      RECIPIENT_ID,
      variables
    );
  }

  @Test
  public void testResetsNumberVariableToZero() {
    var variable = new AutomationNumberVariable(
      new AutomationVariableData(
        VARIABLE_ID,
        "number",
        "counter",
        RECIPIENT_ID,
        "42"
      ),
      dataRepository
    );
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.of(variable)
    );

    action().execute(iteration);

    assertEquals(BigDecimal.ZERO, variable.value());
  }

  @Test
  public void testResetsStringVariableToEmpty() {
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

    action().execute(iteration);

    assertEquals("", variable.value());
  }

  @Test
  public void testDoesNothingWhenVariableNotFound() {
    when(variables.getById(RECIPIENT_ID, VARIABLE_ID)).thenReturn(
      Optional.empty()
    );

    action().execute(iteration);

    verifyNoInteractions(dataRepository);
  }

  @Test
  public void testReadsVariableIdFromActionData() {
    assertEquals(Optional.of(VARIABLE_ID), action().getVariableId());
  }

  @Test
  public void testDefaultsToEmptyWhenVariableIdUnset() {
    var action = new ResetVariableAction(
      new AutomationActionData(ResetVariableAction.ID, Map.of()),
      RECIPIENT_ID,
      variables
    );

    assertTrue(action.getVariableId().isEmpty());
  }
}
