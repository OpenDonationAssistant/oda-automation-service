package io.github.opendonationassistant.automation.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.github.opendonationassistant.automation.AutomationRule;
import io.micronaut.data.model.Pageable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
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
    repository.create(recipientId, id, name, List.of(trigger), List.of(action), true);

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

  @Test
  public void testListByRecipientIdAndTriggerFiltersAndPaginates(
    @Given String recipientId,
    @Given String commandRuleId,
    @Given String otherCommandRuleId,
    @Given String streamRuleId,
    @Given String name,
    @Given AutomationActionData action
  ) {
    repository.create(
      recipientId,
      commandRuleId,
      name,
      List.of(new AutomationTriggerData("command", Map.of("ruleId", commandRuleId))),
      List.of(action),
      true
    );
    repository.create(
      recipientId,
      otherCommandRuleId,
      name,
      List.of(new AutomationTriggerData("command", Map.of("ruleId", otherCommandRuleId))),
      List.of(action),
      true
    );
    repository.create(
      recipientId,
      streamRuleId,
      name,
      List.of(new AutomationTriggerData("stream-started", Map.of())),
      List.of(action),
      true
    );

    final var firstPage = repository.listByRecipientIdAndTrigger(
      recipientId,
      "command",
      Pageable.from(0, 1)
    );

    assertEquals(1, firstPage.getContent().size());
    assertEquals(2L, firstPage.getTotalSize());

    final var streamTriggered = repository.listByRecipientIdAndTrigger(
      recipientId,
      "stream-started",
      Pageable.from(0, 20)
    );

    assertEquals(1, streamTriggered.getContent().size());
    assertEquals(streamRuleId, streamTriggered.getContent().get(0).data().id());
  }
}
