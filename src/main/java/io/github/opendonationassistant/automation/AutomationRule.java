package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.domain.action.ActionFactory;
import io.github.opendonationassistant.automation.domain.trigger.TriggerFactory;
import io.github.opendonationassistant.automation.repository.AutomationRuleData;
import io.github.opendonationassistant.automation.repository.AutomationRuleDataRepository;
import java.util.List;

public class AutomationRule {

  private AutomationRuleData data;
  private final AutomationRuleDataRepository repository;
  private final TriggerFactory triggerFactory;
  private final ActionFactory actionFactory;

  public AutomationRule(
    AutomationRuleDataRepository repository,
    TriggerFactory triggerFactory,
    ActionFactory actionFactory,
    AutomationRuleData data
  ) {
    this.repository = repository;
    this.triggerFactory = triggerFactory;
    this.actionFactory = actionFactory;
    this.data = data;
  }

  public void update(AutomationRuleData data) {
    this.data = data;
    save();
  }

  public AutomationRuleData data() {
    return data;
  }

  public void save() {
    repository.update(data);
  }

  public void delete() {
    repository.deleteById(this.data.id());
  }

  public List<AutomationTrigger> getTriggers() {
    return data.triggers().stream().map(triggerFactory::from).toList();
  }

  public List<AutomationAction> getActions() {
    return data
      .actions()
      .stream()
      .map(action -> actionFactory.from(data.recipientId(), action))
      .toList();
  }
}
