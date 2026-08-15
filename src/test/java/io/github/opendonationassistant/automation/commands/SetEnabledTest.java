package io.github.opendonationassistant.automation.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.micronaut.security.authentication.Authentication;
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
public class SetEnabledTest {

  @Inject
  SetEnabled setEnabled;

  @Inject
  AutomationRuleRepository repository;

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Test
  public void testTogglingRuleEnabled(
    @Given String recipientId,
    @Given String id,
    @Given String name,
    @Given AutomationTriggerData trigger,
    @Given AutomationActionData action
  ) {
    repository.create(recipientId, id, name, List.of(trigger), List.of(action), true);
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", recipientId)
    );

    setEnabled.setEnabled(auth, new SetEnabled.SetEnabledCommand(id));

    final Optional<AutomationRule> rule = repository.getByRecipientIdAndRuleId(
      recipientId,
      id
    );
    assertTrue(rule.isPresent());
    assertFalse(rule.get().data().enabled());

    setEnabled.setEnabled(auth, new SetEnabled.SetEnabledCommand(id));

    final Optional<AutomationRule> toggledBack = repository
      .getByRecipientIdAndRuleId(recipientId, id);
    assertTrue(toggledBack.isPresent());
    assertTrue(toggledBack.get().data().enabled());
  }

  @Test
  public void testToggleRuleOfAnotherUser(
    @Given String recipientId,
    @Given String otherRecipientId,
    @Given String id,
    @Given String name,
    @Given AutomationTriggerData trigger,
    @Given AutomationActionData action
  ) {
    repository.create(recipientId, id, name, List.of(trigger), List.of(action), true);
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", otherRecipientId)
    );

    var response = setEnabled.setEnabled(
      auth,
      new SetEnabled.SetEnabledCommand(id)
    );

    assertEquals(404, response.getStatus().getCode());
    final Optional<AutomationRule> rule = repository.getByRecipientIdAndRuleId(
      recipientId,
      id
    );
    assertTrue(rule.isPresent());
    assertTrue(rule.get().data().enabled());
  }
}
