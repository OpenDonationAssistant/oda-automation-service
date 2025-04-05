package io.github.opendonationassistant.automation.listener;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.automation.domain.action.ActionFactory;
import io.github.opendonationassistant.automation.domain.trigger.TriggerFactory;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.events.goal.GoalSender;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class GoalListener {

  private Logger log = LoggerFactory.getLogger(GoalListener.class);

  private AutomationRuleRepository ruleRepository;
  private TriggerFactory triggerFactory;
  private ActionFactory actionFactory;
  private GoalSender goalSender;
  private WidgetsApi widgets;

  @Inject
  public GoalListener(
    AutomationRuleRepository ruleRepository,
    TriggerFactory triggerFactory,
    ActionFactory actionFactory,
    GoalSender goalSender,
    WidgetsApi widgets
  ) {
    this.triggerFactory = triggerFactory;
    this.actionFactory = actionFactory;
    this.ruleRepository = ruleRepository;
    this.goalSender = goalSender;
    this.widgets = widgets;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Events.GOAL)
  public void checkAutomationForUpdatedGoals(UpdatedGoal goal) {
    final List<AutomationRule> rules = ruleRepository.listByRecipientId(
      goal.getRecipientId()
    );
    try {
      Thread.sleep(3000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    rules.forEach(rule ->
      rule
        .getTriggers()
        .stream()
        .map(trigger ->
          triggerFactory.create(trigger.getId(), trigger.getValue())
        )
        .filter(trigger -> trigger.isTriggered(goal))
        .findAny()
        .ifPresent(trigger -> {
          rule
            .getActions()
            .stream()
            .map(action ->
              actionFactory.create(
                goal.getRecipientId(),
                action.getId(),
                action.getValue()
              )
            )
            .toList()
            .forEach(AutomationAction::execute);
          goalSender.sendUpdatedGoal(goal);
        })
    );
  }
}
