package io.github.opendonationassistant.automation.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationTrigger;
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
    @Given AutomationTrigger trigger,
    @Given AutomationAction action
  ) {
    repository.create(recipientId, id, name, List.of(trigger), List.of(action));
    final Optional<AutomationRule> created =
      repository.getByRecipientIdAndRuleId(recipientId, id);
    assertTrue(created.isPresent());
    assertEquals(recipientId, created.get().getRecipientId());
    assertEquals(id, created.get().getId());
    assertEquals(name, created.get().getName());
    assertEquals(List.of(trigger), created.get().getTriggers());
    assertEquals(List.of(action), created.get().getActions());
  }
}
