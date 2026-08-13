package io.github.opendonationassistant.automation.commands;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.api.SetEnabledApi;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;

@Controller
public class SetEnabled extends BaseController implements SetEnabledApi {

  private final ODALogger log = new ODALogger(this);

  private final AutomationRuleRepository rules;

  @Inject
  public SetEnabled(AutomationRuleRepository rules) {
    this.rules = rules;
  }

  @Override
  public HttpResponse<Void> setEnabled(
    Authentication auth,
    @Body SetEnabledCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    final Optional<AutomationRule> rule = rules.getByRecipientIdAndRuleId(
      ownerId.get(),
      command.ruleId()
    );
    if (rule.isEmpty()) {
      log.info("Rule not found", Map.of("ruleId", command.ruleId()));
      return HttpResponse.notFound();
    }
    log.info(
      "Toggling rule enabled state",
      Map.of("ruleId", command.ruleId(), "enabled", rule.get().data().enabled())
    );
    rule.get().toggleEnabled();
    return HttpResponse.ok();
  }

  @Serdeable
  public static record SetEnabledCommand(String ruleId) {}
}
