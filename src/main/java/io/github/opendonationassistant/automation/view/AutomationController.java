package io.github.opendonationassistant.automation.view;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.api.AutomationOperationsApi;
import io.github.opendonationassistant.automation.dto.AutomationActionDto;
import io.github.opendonationassistant.automation.dto.AutomationDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationTriggerDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.authentication.Authentication;
import java.util.List;
import java.util.Optional;

@Controller
public class AutomationController implements AutomationOperationsApi {

  private AutomationVariableRepository variables;
  private AutomationRuleRepository rules;

  public AutomationController(
    AutomationVariableRepository variables,
    AutomationRuleRepository rules
  ) {
    this.variables = variables;
    this.rules = rules;
  }

  public HttpResponse<List<AutomationVariableDto>> listVariables(
    Authentication auth
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.ok(
      variables
        .listByRecipientId(ownerId.get())
        .stream()
        .map(this::convert)
        .toList()
    );
  }

  @Get("/automation/rules")
  public HttpResponse<List<AutomationRuleDto>> listAutomations(
    Authentication auth
  ) {
    final Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.ok(
      rules
        .listByRecipientId(ownerId.get())
        .stream()
        .map(this::convert)
        .toList()
    );
  }

  private AutomationVariableDto convert(AutomationVariable<?> variable) {
    return new AutomationVariableDto(
      variable.data().id(),
      variable.data().name(),
      variable.data().type(),
      variable.data().value()
    );
  }

  private AutomationRuleDto convert(AutomationRule rule) {
    return new AutomationRuleDto(
      rule.data().id(),
      rule.data().name(),
      rule
        .data()
        .triggers()
        .stream()
        .map(trigger -> new AutomationTriggerDto(trigger.id(), trigger.value()))
        .toList(),
      rule
        .data()
        .actions()
        .stream()
        .map(action -> new AutomationActionDto(action.id(), action.value()))
        .toList()
    );
  }

  @Get("/automation/")
  public HttpResponse<AutomationDto> getState(Authentication auth) {
    final Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.ok(
      new AutomationDto(
        rules
          .listByRecipientId(ownerId.get())
          .stream()
          .map(this::convert)
          .toList(),
        variables
          .listByRecipientId(ownerId.get())
          .stream()
          .map(this::convert)
          .toList()
      )
    );
  }
}
