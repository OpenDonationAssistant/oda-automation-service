package io.github.opendonationassistant.automation.listener;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.domain.action.ActionFactory;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.automation.domain.trigger.TriggerFactory;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.goal.GoalSender;
import io.github.opendonationassistant.events.goal.GoalSender.Stage;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@RabbitListener
public class GoalListener {

  private final ODALogger log = new ODALogger(this);

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
  public void checkAutomationForUpdatedGoals(UpdatedGoal updated) {
    final List<AutomationRule> rules = ruleRepository.listByRecipientId(
      updated.recipientId()
    );

    log.info(
      "Handling UpdatedGoal",
      Map.of("goal", updated, "rules", rules)
    );

    var goal = new Goal(
      updated.goalId(),
      updated.widgetId(),
      updated.recipientId(),
      updated.fullDescription(),
      updated.briefDescription(),
      updated.requiredAmount(),
      updated.accumulatedAmount(),
      updated.isDefault()
    );

    Goal updatedGoal = goal;
    do {
      updatedGoal = goal;
      goal = process(updatedGoal, rules);
    } while (checkHasChanges(updatedGoal, goal));

    updateGoal(goal);
  }

  private Goal process(Goal goal, List<AutomationRule> rules) {
    var updatedGoal = new Goal(
      goal.getGoalId(),
      goal.getWidgetId(),
      goal.getRecipientId(),
      goal.getFullDescription(),
      goal.getBriefDescription(),
      new Amount(
        goal.getRequiredAmount().getMajor(),
        goal.getRequiredAmount().getMinor(),
        goal.getRequiredAmount().getCurrency()
      ),
      new Amount(
        goal.getAccumulatedAmount().getMajor(),
        goal.getAccumulatedAmount().getMinor(),
        goal.getAccumulatedAmount().getCurrency()
      ),
      goal.getIsDefault()
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
                goal.getRecipientId(),
                action.getId(),
                action.getValue(),
                updatedGoal
              )
            )
            .toList()
            .forEach(AutomationAction::execute);
        })
    );
    return updatedGoal;
  }

  private boolean checkHasChanges(Goal origin, Goal updated) {
    boolean accumulatedChanged =
      updated.getAccumulatedAmount().getMajor() !=
      origin.getAccumulatedAmount().getMajor();
    boolean requiredChanged =
      updated.getRequiredAmount().getMajor() !=
      origin.getRequiredAmount().getMajor();
    return (accumulatedChanged || requiredChanged);
  }

  private void updateGoal(Goal goal) {
    goalSender.sendGoal(
      Stage.AFTER_AUTOMATION,
      new UpdatedGoal(
        goal.getGoalId(),
        goal.getWidgetId(),
        goal.getRecipientId(),
        goal.getFullDescription(),
        goal.getBriefDescription(),
        goal.getRequiredAmount(),
        goal.getAccumulatedAmount(),
        goal.getIsDefault()
      )
    );
  }
}
