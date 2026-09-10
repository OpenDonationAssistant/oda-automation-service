package io.github.opendonationassistant.automation.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.api.TriggerRuleApi.TriggerRuleCommand;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class TriggerRuleTest {

  @Inject
  TriggerRule triggerRule;

  @Inject
  AutomationRuleRepository repository;

  @Inject
  MeterRegistry registry;

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Test
  public void testTriggeringRuleByCommand(
    @Given String recipientId,
    @Given String id,
    @Given String name
  ) {
    repository.create(
      recipientId,
      id,
      name,
      List.of(new AutomationTriggerData("command", Map.of("ruleId", id))),
      List.of(),
      true
    );
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", recipientId)
    );

    var before = registry
      .counter("automation.iteration.runs", "source", "TriggerRuleCommand")
      .count();
    var response = triggerRule.triggerRule(
      auth,
      new TriggerRuleCommand(id, "nickname", "system", Map.of())
    );
    var after = registry
      .counter("automation.iteration.runs", "source", "TriggerRuleCommand")
      .count();

    assertEquals(200, response.getStatus().getCode());
    assertEquals(before + 1, after);
  }

  @Test
  public void testTriggeringRuleOfAnotherUser(
    @Given String recipientId,
    @Given String otherRecipientId,
    @Given String id,
    @Given String name
  ) {
    repository.create(
      recipientId,
      id,
      name,
      List.of(new AutomationTriggerData("command", Map.of("ruleId", id))),
      List.of(),
      true
    );
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", otherRecipientId)
    );

    var response = triggerRule.triggerRule(
      auth,
      new TriggerRuleCommand(id, "nickname", "system", Map.of())
    );

    assertEquals(401, response.getStatus().getCode());
  }

  @Test
  public void testTriggeringRuleWithoutOwnerId(@Given String id) {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(Map.of());

    var response = triggerRule.triggerRule(
      auth,
      new TriggerRuleCommand(id, "nickname", "system", Map.of())
    );

    assertEquals(401, response.getStatus().getCode());
  }
}

