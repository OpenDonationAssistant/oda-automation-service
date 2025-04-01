package io.github.opendonationassistant.automation.view;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.dto.AutomationDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;
import java.util.Optional;

@Controller
public class AutomationController {

  private AutomationVariableRepository variables;
  private AutomationRuleRepository rules;

  public AutomationController(
    AutomationVariableRepository variables,
    AutomationRuleRepository rules
  ) {
    this.variables = variables;
    this.rules = rules;
  }

  @Get("/automation/variables")
  @Secured(SecurityRule.IS_AUTHENTICATED)
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
        .map(AutomationVariable::asDto)
        .toList()
    );
  }

  @Get("/automation/rules")
  @Secured(SecurityRule.IS_AUTHENTICATED)
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
        .map(AutomationRule::asDto)
        .toList()
    );
  }

  @Get("/automation/")
  @Secured(SecurityRule.IS_AUTHENTICATED)
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
          .map(AutomationRule::asDto)
          .toList(),
        variables
          .listByRecipientId(ownerId.get())
          .stream()
          .map(AutomationVariable::asDto)
          .toList()
      )
    );
  }

  private Optional<String> getOwnerId(Authentication auth) {
    return Optional.ofNullable(
      String.valueOf(auth.getAttributes().get("preferred_username"))
    );
  }
}
