package io.github.opendonationassistant.automation.domain;

import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class IterationFactory {

  private final AutomationRuleRepository ruleRepository;
  private final AutomationVariableRepository variableRepository;

  @Inject
  public IterationFactory(
    AutomationRuleRepository ruleRepository,
    AutomationVariableRepository variableRepository
  ) {
    this.ruleRepository = ruleRepository;
    this.variableRepository = variableRepository;
  }

  public Iteration create(String recipientId, Object source) {
    return new Iteration(
      recipientId,
      source,
      variableRepository.listByRecipientId(recipientId),
      ruleRepository.listByRecipientId(recipientId)
    );
  }
}
