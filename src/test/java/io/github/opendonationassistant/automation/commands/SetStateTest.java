package io.github.opendonationassistant.automation.commands;

import static org.instancio.Select.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.dto.AutomationDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.view.AutomationController;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.instancio.Instancio;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.BeforeEach;
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

  private final RabbitClient rabbit = mock(RabbitClient.class);

  @MockBean(value = RabbitClient.class, named = "automation")
  @Named("automation")
  RabbitClient automationRabbitClient() {
    return rabbit;
  }

  @BeforeEach
  void clearRabbitInvocations() {
    clearInvocations(rabbit);
  }

  @Test
  public void testCreatingState(
    @Given String recipientId,
    @Given AutomationRuleDto rule
  ) throws Exception {
    var variable = Instancio.of(AutomationVariableDto.class)
      .set(field(AutomationVariableDto::type), "string")
      .create();
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", recipientId)
    );

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
    assertEquals(List.of(rule), state.get().rules());
    assertEquals(List.of(variable), state.get().variables());

    verify(rabbit).sendEvent(
      new AutomationVariable.VariableCreated(
        variable.id(),
        recipientId,
        variable.name(),
        variable.type(),
        variable.value()
      )
    );
  }

  @Test
  public void testUpdatingExistingVariableSendsUpdatedEvent(
    @Given String recipientId,
    @Given String newName,
    @Given String newValue
  ) throws Exception {
    var variable = Instancio.of(AutomationVariableDto.class)
      .set(field(AutomationVariableDto::type), "string")
      .create();
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", recipientId)
    );
    setState.setState(
      auth,
      new SetState.SetStateCommand(List.of(), List.of(variable))
    );
    clearInvocations(rabbit);

    var updated = new AutomationVariableDto(
      variable.id(),
      newName,
      variable.type(),
      newValue
    );
    setState.setState(
      auth,
      new SetState.SetStateCommand(List.of(), List.of(updated))
    );

    verify(rabbit).sendEvent(
      new AutomationVariable.VariableUpdated(
        variable.id(),
        recipientId,
        newName,
        variable.type(),
        newValue
      )
    );
  }

  @Test
  public void testUpdatingState(
    @Given String recipientId,
    @Given AutomationRuleDto rule1,
    @Given AutomationRuleDto rule2,
    @Given AutomationRuleDto rule3
  ) throws Exception {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", recipientId)
    );

    var variable1 = Instancio.of(AutomationVariableDto.class)
      .set(field(AutomationVariableDto::type), "string")
      .create();
    var variable2 = Instancio.of(AutomationVariableDto.class)
      .set(field(AutomationVariableDto::type), "string")
      .create();
    var variable3 = Instancio.of(AutomationVariableDto.class)
      .set(field(AutomationVariableDto::type), "string")
      .create();

    var command = new SetState.SetStateCommand(
      List.of(rule1),
      List.of(variable1)
    );
    setState.setState(auth, command);
    clearInvocations(rabbit);

    var updateCommand = new SetState.SetStateCommand(
      List.of(rule2, rule3),
      List.of(variable2, variable3)
    );

    setState.setState(auth, updateCommand);

    @NonNull
    final Optional<AutomationDto> state = viewController
      .getState(auth)
      .getBody(AutomationDto.class);

    assertTrue(state.isPresent());
    assertEquals(List.of(rule2, rule3), state.get().rules());
    assertEquals(List.of(variable2, variable3), state.get().variables());

    verify(rabbit).sendEvent(
      new AutomationVariable.VariableDeleted(
        variable1.id(),
        recipientId,
        variable1.name(),
        variable1.type(),
        variable1.value()
      )
    );
    verify(rabbit).sendEvent(
      new AutomationVariable.VariableCreated(
        variable2.id(),
        recipientId,
        variable2.name(),
        variable2.type(),
        variable2.value()
      )
    );
    verify(rabbit).sendEvent(
      new AutomationVariable.VariableCreated(
        variable3.id(),
        recipientId,
        variable3.name(),
        variable3.type(),
        variable3.value()
      )
    );
  }
}
