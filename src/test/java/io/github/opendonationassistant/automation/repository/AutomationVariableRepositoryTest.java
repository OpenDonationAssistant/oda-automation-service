package io.github.opendonationassistant.automation.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class AutomationVariableRepositoryTest {

  private ODALogger log = new ODALogger(AutomationVariableRepositoryTest.class);

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
    log.debug("Created string variable", Map.of("variable", createdString));

    assertTrue(createdString instanceof AutomationStringVariable);
    assertTrue(
      createdString.data().id() != null && !createdString.data().id().isEmpty()
    );
    assertEquals("string", createdString.data().type());
    assertEquals(recipientId, createdString.data().recipientId());
    assertEquals("", createdString.value());
    assertEquals("<Без названия>", createdString.data().name());

    final AutomationVariable<?> createdNumber = repository.create(
      recipientId,
      "number",
      null,
      null,
      null
    );

    assertTrue(createdNumber instanceof AutomationNumberVariable);
    assertTrue(
      createdNumber.data().id() != null && !createdNumber.data().id().isEmpty()
    );
    assertEquals("number", createdNumber.data().type());
    assertEquals(recipientId, createdNumber.data().recipientId());
    assertEquals("<Без названия>", createdString.data().name());
    assertEquals(BigDecimal.ZERO, createdNumber.value());
  }

  @Test
  public void testCreatingStringVariableWithIdAndValue(
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
    log.debug("Created string variable", Map.of("variable", createdString));
    assertEquals(variableId, createdString.data().id());
    assertEquals("string", createdString.data().type());
    assertEquals(recipientId, createdString.data().recipientId());
    assertEquals(value, createdString.value());
  }

  @Test
  public void testCreatingNumberVariableWithIdAndValue(
    @Given String recipientId,
    @Given String variableId,
    @Given String name
  ) {
    BigDecimal value = new BigDecimal("123.45");
    final AutomationVariable<?> created = repository.create(
      recipientId,
      "number",
      variableId,
      name,
      value.toString()
    );
    log.debug("Created number variable", Map.of("variable", created));
    assertEquals(variableId, created.data().id());
    assertEquals("number", created.data().type());
    assertEquals(recipientId, created.data().recipientId());
    assertEquals(value, created.value());
  }

  @Test
  public void testUpdatingStringVariable(
    @Given String recipientId,
    @Given String newName,
    @Given String newValue
  ) {
    var created = repository.create(recipientId, "string", null, null, null);
    assertTrue(created instanceof AutomationStringVariable);
    if (created instanceof AutomationStringVariable createdString) {
      createdString.update(newName, newValue);
      final Optional<AutomationVariable<?>> updated = repository.getById(
        recipientId,
        createdString.data().id()
      );
      assertTrue(updated.isPresent());
      final AutomationVariable<?> updatedString = updated.get();
      assertEquals(recipientId, updatedString.data().recipientId());
      assertEquals(createdString.data().id(), updatedString.data().id());
      assertEquals(newName, updatedString.data().name());
      assertEquals(newValue, updatedString.data().value());
    }
  }

  @Test
  public void testUpdatingNumberVariable(
    @Given String recipientId,
    @Given String newName,
    @Given BigDecimal newValue
  ) {
    var created = repository.create(recipientId, "number", null, null, null);
    assertTrue(created instanceof AutomationNumberVariable);
    if (created instanceof AutomationNumberVariable createdNumber) {
      createdNumber.update(newName, newValue);
      final Optional<AutomationVariable<?>> updated = repository.getById(
        recipientId,
        createdNumber.data().id()
      );
      assertTrue(updated.isPresent());
      final AutomationVariable<?> updatedString = updated.get();
      assertEquals(recipientId, updatedString.data().recipientId());
      assertEquals(createdNumber.data().id(), updatedString.data().id());
      assertEquals(newName, updatedString.data().name());
      assertEquals(newValue, updatedString.value());
    }
  }

  @Test
  public void testDeletingVariable(@Given String recipientId) {
    var createdString = repository.create(
      recipientId,
      "string",
      null,
      null,
      null
    );
    createdString.delete();
    final Optional<AutomationVariable<?>> updatedString = repository.getById(
      recipientId,
      createdString.data().id()
    );
    assertTrue(updatedString.isEmpty());
  }
}
