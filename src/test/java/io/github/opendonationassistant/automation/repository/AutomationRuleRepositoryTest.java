package io.github.opendonationassistant.automation.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.github.opendonationassistant.automation.AutomationRule;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class AutomationRuleRepositoryTest {

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Inject
  public AutomationRuleRepository repository;

  @Test
  public void testCreateAndReadRule(
    @Given String recipientId,
    @Given String id,
    @Given String name,
    @Given AutomationTriggerData trigger,
    @Given AutomationActionData action
  ) {
    repository.create(recipientId, id, name, List.of(trigger), List.of(action));

    final Optional<AutomationRule> optionallyCreated =
      repository.getByRecipientIdAndRuleId(recipientId, id);
    assertTrue(optionallyCreated.isPresent());
    var created = optionallyCreated.get();
    assertEquals(recipientId, created.data().recipientId());
    assertEquals(id, created.data().id());
    assertEquals(name, created.data().name());
    assertEquals(List.of(trigger), created.data().triggers());
    assertEquals(List.of(action), created.data().actions());
  }
}
