package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.EphemeralVariable;
import io.github.opendonationassistant.automation.api.TriggerRuleApi.TriggerRuleCommand;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import java.util.Optional;

public class CommandTrigger extends AutomationTrigger {

  public CommandTrigger(AutomationTriggerData data) {
    super(data);
  }

  public Optional<String> getRuleId() {
    return Optional.ofNullable((String) this.data().value().get("ruleId"));
  }

  @Override
  public boolean isTriggered(Object target) {
    if (target instanceof TriggerRuleCommand command) {
      return getRuleId()
        .map(ruleId -> ruleId.equals(command.id()))
        .orElse(false);
    }
    return false;
  }

  @Override
  public void extractVariables(Object target, Iteration iteration) {
    if (target instanceof TriggerRuleCommand command) {
      if (command.nickname() != null) {
        iteration.add(
          new EphemeralVariable<String>("nickname", command.nickname())
        );
      }
      if (command.system() != null) {
        iteration.add(
          new EphemeralVariable<String>("system", command.system())
        );
      }
      if (command.variables() != null) {
        command
          .variables()
          .forEach((key, value) ->
            iteration.add(new EphemeralVariable<String>(key, value))
          );
      }
    }
  }
}
