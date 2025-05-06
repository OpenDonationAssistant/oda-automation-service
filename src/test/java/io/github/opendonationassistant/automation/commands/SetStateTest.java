package io.github.opendonationassistant.automation.commands;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.automation.dto.AutomationDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.view.AutomationController;
import io.micronaut.core.annotation.NonNull;
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
public class SetStateTest {

  @Inject
  SetState setState;

  @Inject
  AutomationController viewController;

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Test
  public void testCreatingState(
    @Given String recipientId,
    @Given AutomationRuleDto rule,
    @Given AutomationVariableDto variable
  ) {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", recipientId)
    );

    variable.setType("string");
    var command = new SetState.SetStateCommand(
      List.of(rule),
      List.of(variable)
    );

    setState.setState(auth, command);

    @NonNull
    final Optional<AutomationDto> state = viewController
      .getState(auth)
      .getBody(AutomationDto.class);

    assertTrue(state.isPresent());
    assertThat(state.get().getRules()).isEqualTo(List.of(rule));
    assertThat(state.get().getVariables()).isEqualTo(List.of(variable));
  }

  @Test
  public void testUpdatingState(
    @Given String recipientId,
    @Given AutomationRuleDto rule1,
    @Given AutomationRuleDto rule2,
    @Given AutomationRuleDto rule3,
    @Given AutomationVariableDto variable1,
    @Given AutomationVariableDto variable2,
    @Given AutomationVariableDto variable3
  ) {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", recipientId)
    );

    variable1.setType("string");
    variable2.setType("string");
    variable3.setType("string");

    var command = new SetState.SetStateCommand(
      List.of(rule1),
      List.of(variable1)
    );
    setState.setState(auth, command);

    var updateCommand = new SetState.SetStateCommand(
      List.of(rule2,rule3),
      List.of(variable2,variable3)
    );

    setState.setState(auth, updateCommand);
    @NonNull
    final Optional<AutomationDto> state = viewController
      .getState(auth)
      .getBody(AutomationDto.class);

    assertTrue(state.isPresent());
    assertThat(state.get().getRules()).isEqualTo(List.of(rule2,rule3));
    assertThat(state.get().getVariables()).isEqualTo(List.of(variable2, variable3));

  }
}
