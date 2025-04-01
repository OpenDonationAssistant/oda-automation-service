package io.github.opendonationassistant.automation.repository;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.repository.AutomationRuleData.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationRuleData.AutomationTriggerData;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class AutomationRuleRepository {

  private AutomationRuleDataRepository repository;

  @Inject
  public AutomationRuleRepository(AutomationRuleDataRepository repository) {
    this.repository = repository;
  }

  public List<AutomationRule> listByRecipientId(String recipientId) {
    return repository
      .getByRecipientId(recipientId)
      .stream()
      .map(rule -> rule.asDomain(repository))
      .toList();
  }

  public Optional<AutomationRule> getByRecipientIdAndRuleId(
    String recipientId,
    String ruleId
  ) {
    return repository
      .getByRecipientIdAndId(recipientId, ruleId)
      .map(rule -> rule.asDomain(repository));
  }

  public void create(
    String recipientId,
    String id,
    String name,
    List<AutomationTrigger> triggers,
    List<AutomationAction> actions
  ) {
    final List<AutomationTriggerData> triggersData = triggers
      .stream()
      .map(AutomationTrigger::asData)
      .toList();
    final List<AutomationActionData> actionsData = actions
      .stream()
      .map(AutomationAction::asData)
      .toList();
    var data = new AutomationRuleData(
      id,
      name,
      recipientId,
      triggersData,
      actionsData
    );
    repository.save(data);
  }
}
