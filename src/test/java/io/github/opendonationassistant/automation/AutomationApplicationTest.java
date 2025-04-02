package io.github.opendonationassistant.automation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.automation.commands.AutomationCommandController;
import io.github.opendonationassistant.automation.commands.SetStateCommand;
import io.github.opendonationassistant.automation.dto.AutomationDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.view.AutomationController;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class AutomationApplicationTest {

  @Inject
  AutomationCommandController commandController;

  @Inject
  AutomationController viewController;

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Test
  public void testSettingNewState(
    @Given String recipientId,
    @Given AutomationRuleDto rule,
    @Given AutomationVariableDto variable
  ) {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", recipientId)
    );

    variable.setType("string");
    var command = new SetStateCommand(List.of(rule), List.of(variable));

    commandController.setState(auth, command);

    @NonNull
    final Optional<AutomationDto> state = viewController
      .getState(auth)
      .getBody(AutomationDto.class);
    assertTrue(state.isPresent());
    assertThat(state.get().getRules()).isEqualTo(List.of(rule));
    assertThat(state.get().getVariables()).isEqualTo(List.of(variable));
  }
}
