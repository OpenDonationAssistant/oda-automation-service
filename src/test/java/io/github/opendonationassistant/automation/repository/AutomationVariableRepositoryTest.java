package io.github.opendonationassistant.automation.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class AutomationVariableRepositoryTest {

  private Logger log = LoggerFactory.getLogger(
    AutomationVariableRepositoryTest.class
  );

  @Inject
  AutomationVariableRepository repository;

  @Test
  public void testCreatingAutomationVariable(@Given String recipientId) {
    final AutomationVariable<?> createdString = repository.create(
      recipientId,
      "string",
      null,
      null,
      null
    );
    log.debug("created string variable: {}", createdString);
    final AutomationVariable<?> createdNumber = repository.create(
      recipientId,
      "number",
      null,
      null,
      null
    );

    assertFalse(createdString.getId().isBlank());
    assertEquals(recipientId, createdString.getRecipientId());
    assertEquals("", createdString.getValue());

    assertFalse(createdNumber.getId().isBlank());
    assertEquals(recipientId, createdNumber.getRecipientId());
    assertEquals(BigDecimal.ZERO, createdNumber.getValue());
  }

  @Test
  public void testCreatingVariableWithIdAndValue(
    @Given String recipientId,
    @Given String variableId,
    @Given String name,
    @Given String value
  ) {
    final AutomationVariable<?> createdString = repository.create(
      recipientId,
      "string",
      variableId,
      name,
      value
    );
    log.debug("created string variable: {}", createdString);
    assertEquals(variableId, createdString.getId());
    assertEquals(recipientId, createdString.getRecipientId());
    assertEquals(value, createdString.getValue());
  }

  @Test
  public void testUpdatingVariable(
    @Given String recipientId,
    @Given String newName,
    @Given String newValue
  ) {
    final AutomationVariable<String> createdString = (AutomationVariable<
        String
      >) repository.create(recipientId, "string", null, null, null);
    createdString.setName(newName);
    createdString.setValue(newValue);
    createdString.save();

    final Optional<AutomationVariable<?>> updatedString = repository.getById(
      recipientId,
      createdString.getId()
    );
    assertTrue(updatedString.isPresent());
    final AutomationVariable<?> updated = updatedString.get();
    assertEquals(recipientId, updated.getRecipientId());
    assertEquals(createdString.getId(), updated.getId());
    assertEquals(newName, updated.getName());
    assertEquals(newValue, updated.getValue());
  }

  @Test
  public void testDeletingVariable(@Given String recipientId) {
    final AutomationVariable<String> createdString = (AutomationVariable<
        String
      >) repository.create(recipientId, "string", null, null, null);
    createdString.delete();
    final Optional<AutomationVariable<?>> updatedString = repository.getById(
      recipientId,
      createdString.getId()
    );
    assertTrue(updatedString.isEmpty());
  }
}
