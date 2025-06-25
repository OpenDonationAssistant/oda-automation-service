package io.github.opendonationassistant.automation.listener;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.domain.action.ActionFactory;
import io.github.opendonationassistant.automation.domain.trigger.TriggerFactory;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.goal.GoalSender;
import io.github.opendonationassistant.events.goal.GoalSender.Stage;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.List;

@RabbitListener
public class GoalListener {

  private final AutomationRuleRepository ruleRepository;
  private final TriggerFactory triggerFactory;
  private final ActionFactory actionFactory;
  private final GoalSender goalSender;

  @Inject
  public GoalListener(
    AutomationRuleRepository ruleRepository,
    TriggerFactory triggerFactory,
    ActionFactory actionFactory,
    GoalSender goalSender
  ) {
    this.triggerFactory = triggerFactory;
    this.actionFactory = actionFactory;
    this.ruleRepository = ruleRepository;
    this.goalSender = goalSender;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Automation.GOAL)
  public void checkAutomationForUpdatedGoals(UpdatedGoal goal) {
    final List<AutomationRule> rules = ruleRepository.listByRecipientId(
      goal.recipientId()
    );

    UpdatedGoal updatedGoal = goal;
    do {
      updatedGoal = goal;
      goal = process(updatedGoal, rules);
    } while (checkHasChanges(updatedGoal, goal));

    updateGoal(goal);
  }

  private UpdatedGoal process(UpdatedGoal goal, List<AutomationRule> rules) {
    var updatedGoal = new UpdatedGoal(
      goal.goalId(),
      goal.widgetId(),
      goal.recipientId(),
      goal.fullDescription(),
      goal.briefDescription(),
      new Amount(
        goal.requiredAmount().getMajor(),
        goal.requiredAmount().getMinor(),
        goal.requiredAmount().getCurrency()
      ),
      new Amount(
        goal.accumulatedAmount().getMajor(),
        goal.accumulatedAmount().getMinor(),
        goal.accumulatedAmount().getCurrency()
      ),
      goal.isDefault()
    );

    rules.forEach(rule ->
      rule
        .getTriggers()
        .stream()
        .map(trigger ->
          triggerFactory.create(trigger.getId(), trigger.getValue())
        )
        .filter(trigger -> trigger.isTriggered(updatedGoal))
        .findAny()
        .ifPresent(trigger -> {
          rule
            .getActions()
            .stream()
            .map(action ->
              actionFactory.create(
                goal.recipientId(),
                action.getId(),
                action.getValue(),
                goal,
                updatedGoal
              )
            )
            .toList()
            .forEach(AutomationAction::execute);
        })
    );
    return updatedGoal;
  }

  private boolean checkHasChanges(UpdatedGoal origin, UpdatedGoal updated) {
    boolean accumulatedChanged =
      updated.accumulatedAmount().getMajor() !=
      origin.accumulatedAmount().getMajor();
    boolean requiredChanged =
      updated.requiredAmount().getMajor() != origin.requiredAmount().getMajor();
    return (accumulatedChanged || requiredChanged);
  }

  private void updateGoal(UpdatedGoal goal) {
    goalSender.sendGoal(Stage.AFTER_AUTOMATION, goal);
  }
}
