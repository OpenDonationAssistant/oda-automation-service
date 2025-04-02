package io.github.opendonationassistant.automation.commands;

import io.github.opendonationassistant.automation.repository.AutomationRuleDataRepository;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import java.util.Optional;

@Controller
public class AutomationCommandController {

  private AutomationVariableRepository variables;
  private AutomationRuleRepository rules;
  private AutomationRuleDataRepository ruleDataRepository;
  private AutomationVariableDataRepository variableDataRepository;

  @Inject
  public AutomationCommandController(
    AutomationVariableRepository variables,
    AutomationRuleRepository rules,
    AutomationRuleDataRepository ruleDataRepository,
    AutomationVariableDataRepository variableDataRepository
  ) {
    this.variables = variables;
    this.rules = rules;
    this.variableDataRepository = variableDataRepository;
    this.ruleDataRepository = ruleDataRepository;
  }

  @Post("/automation/commands/setstate")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> setState(
    Authentication auth,
    @Body SetStateCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    command.execute(
      variables,
      rules,
      ruleDataRepository,
      variableDataRepository,
      ownerId.get()
    );
    return HttpResponse.ok();
  }

  private Optional<String> getOwnerId(Authentication auth) {
    return Optional.ofNullable(
      String.valueOf(auth.getAttributes().get("preferred_username"))
    );
  }
}
