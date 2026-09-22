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
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.validation.Validated;
import java.util.Optional;

@Controller
@Validated
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

  public HttpResponse<Page<AutomationVariableDto>> listVariables(
    Authentication auth,
    Pageable pageable
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    if (pageable.isUnpaged()) {
      pageable = Pageable.from(0, 20);
    }
    return HttpResponse.ok(
      variables
        .listByRecipientId(ownerId.get(), pageable)
        .map(this::convert)
    );
  }

  @Get("/automation/rules")
  public HttpResponse<Page<AutomationRuleDto>> listAutomations(
    Authentication auth,
    Pageable pageable
  ) {
    final Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    if (pageable.isUnpaged()) {
      pageable = Pageable.from(0, 20);
    }
    return HttpResponse.ok(
      rules.listByRecipientId(ownerId.get(), pageable).map(this::convert)
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
        .toList(),
      rule.data().enabled()
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