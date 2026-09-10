package io.github.opendonationassistant.automation.commands;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.api.TriggerRuleApi;
import io.github.opendonationassistant.automation.domain.IterationFactory;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;

@Controller
@Validated
public class TriggerRule extends BaseController implements TriggerRuleApi {

  private final ODALogger log = new ODALogger(this);

  private final AutomationRuleRepository rules;
  private final IterationFactory iterationFactory;

  @Inject
  public TriggerRule(
    AutomationRuleRepository rules,
    IterationFactory iterationFactory
  ) {
    this.rules = rules;
    this.iterationFactory = iterationFactory;
  }

  @Override
  public HttpResponse<Void> triggerRule(
    Authentication auth,
    @Valid @Body TriggerRuleCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    final Optional<AutomationRule> rule = rules.getByRecipientIdAndRuleId(
      ownerId.get(),
      command.id()
    );
    if (rule.isEmpty()) {
      log.info("Rule not found", Map.of("ruleId", command.id()));
      return HttpResponse.notFound();
    }
    log.info("Triggering rule by command", Map.of("command", command));
    iterationFactory.create(ownerId.get(), command).run();
    return HttpResponse.ok();
  }
}
