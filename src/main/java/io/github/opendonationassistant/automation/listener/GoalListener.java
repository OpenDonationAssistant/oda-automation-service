package io.github.opendonationassistant.automation.listener;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import java.util.List;

@RabbitListener
public class GoalListener {

  private AutomationRuleRepository ruleRepository;

  @Queue(io.github.opendonationassistant.rabbit.Queue.Events.GOAL)
  public void checkAutomationForUpdatedGoals(UpdatedGoal goal) {
    final List<AutomationRule> rules = ruleRepository.listByRecipientId(
      goal.getRecipientId()
    );
    rules.forEach(rule ->
      rule
        .getTriggers()
        .stream()
        .filter(trigger -> trigger.isTriggered(goal))
        .findAny()
        .ifPresent(trigger -> {
          rule.getActions().forEach(AutomationAction::execute);
        })
    );
  }
}
