package io.github.opendonationassistant.automation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.github.opendonationassistant.rabbit.RabbitClient;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class AutomationVariableTest {

  private static final String RECIPIENT_ID = "recipient-1";
  private static final String VARIABLE_ID = "variable-1";

  private final AutomationVariableDataRepository dataRepository = mock(
    AutomationVariableDataRepository.class
  );
  private final RabbitClient rabbit = mock(RabbitClient.class);
  private final AutomationVariableData data = new AutomationVariableData(
    VARIABLE_ID,
    "string",
    "greeting",
    RECIPIENT_ID,
    "hello"
  );

  private AutomationVariable<String> variable() {
    return new AutomationVariable<>(data, dataRepository, rabbit);
  }

  @Test
  public void testCreatesAndPublishesCreatedEventWhenMissing() throws Exception {
    when(
      dataRepository.getByRecipientIdAndId(RECIPIENT_ID, VARIABLE_ID)
    ).thenReturn(Optional.empty());

    variable().save();

    verify(dataRepository).save(data);
    verify(dataRepository, never()).update(data);
    verify(rabbit).sendEvent(
      new AutomationVariable.VariableCreated(
        VARIABLE_ID,
        RECIPIENT_ID,
        "greeting",
        "string",
        "hello"
      )
    );
  }

  @Test
  public void testUpdatesAndPublishesUpdatedEventWhenExisting() throws Exception {
    when(
      dataRepository.getByRecipientIdAndId(RECIPIENT_ID, VARIABLE_ID)
    ).thenReturn(Optional.of(data));

    variable().save();

    verify(dataRepository).update(data);
    verify(dataRepository, never()).save(data);
    verify(rabbit).sendEvent(
      new AutomationVariable.VariableUpdated(
        VARIABLE_ID,
        RECIPIENT_ID,
        "greeting",
        "string",
        "hello"
      )
    );
  }

  @Test
  public void testDeletesAndPublishesDeletedEventWhenExisting() throws Exception {
    when(
      dataRepository.getByRecipientIdAndId(RECIPIENT_ID, VARIABLE_ID)
    ).thenReturn(Optional.of(data));

    variable().delete();

    verify(dataRepository).deleteById(VARIABLE_ID);
    verify(rabbit).sendEvent(
      new AutomationVariable.VariableDeleted(
        VARIABLE_ID,
        RECIPIENT_ID,
        "greeting",
        "string",
        "hello"
      )
    );
  }

  @Test
  public void testDoesNotDeleteOrPublishWhenMissing() throws Exception {
    when(
      dataRepository.getByRecipientIdAndId(RECIPIENT_ID, VARIABLE_ID)
    ).thenReturn(Optional.empty());

    variable().delete();

    verify(dataRepository, never()).deleteById(VARIABLE_ID);
    verify(rabbit, never()).sendEvent(any());
  }
}
