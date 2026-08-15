package io.github.opendonationassistant.automation.repository;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.domain.action.ActionFactory;
import io.github.opendonationassistant.automation.domain.trigger.TriggerFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Singleton
public class AutomationRuleRepository {

  private AutomationRuleDataRepository repository;
  private TriggerFactory triggerFactory;
  private ActionFactory actionFactory;

  @Inject
  public AutomationRuleRepository(
    AutomationRuleDataRepository repository,
    TriggerFactory triggerFactory,
    ActionFactory actionFactory
  ) {
    this.repository = repository;
    this.triggerFactory = triggerFactory;
    this.actionFactory = actionFactory;
  }

  public Stream<AutomationRule> listByRecipientId(String recipientId) {
    return repository
      .getByRecipientId(recipientId)
      .stream()
      .map(this::convert);
  }

  private AutomationRule convert(AutomationRuleData data) {
    return new AutomationRule(repository, triggerFactory, actionFactory, data);
  }

  public Optional<AutomationRule> getByRecipientIdAndRuleId(
    String recipientId,
    String ruleId
  ) {
    return repository
      .getByRecipientIdAndId(recipientId, ruleId)
      .map(this::convert);
  }

  public void create(
    String recipientId,
    String id,
    String name,
    List<AutomationTriggerData> triggers,
    List<AutomationActionData> actions,
    boolean enabled
  ) {
    repository.save(
      new AutomationRuleData(id, name, recipientId, triggers, actions, enabled)
    );
  }
}
