package io.github.opendonationassistant.automation.listener;

import java.util.Map;

import io.github.opendonationassistant.automation.domain.IterationFactory;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import io.github.opendonationassistant.events.goal.UpdatedGoalSender;
import io.github.opendonationassistant.events.goal.UpdatedGoalSender.Stage;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;

@RabbitListener
public class GoalListener {

  private final ODALogger log = new ODALogger(this);

  private final IterationFactory iterationFactory;
  private final UpdatedGoalSender goalSender;

  @Inject
  public GoalListener(
    IterationFactory iterationFactory,
    UpdatedGoalSender goalSender
  ) {
    this.iterationFactory = iterationFactory;
    this.goalSender = goalSender;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Automation.GOAL)
  public void checkAutomationForUpdatedGoals(UpdatedGoal updated) {
    log.info("Handling UpdatedGoal", Map.of("goal", updated));

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
      goal = process(updatedGoal);
    } while (checkHasChanges(updatedGoal, goal));

    updateGoal(goal);
  }

  private Goal process(Goal goal) {
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

    iterationFactory.create(goal.getRecipientId(), updatedGoal).run();

    return updatedGoal;
  }

  private boolean checkHasChanges(Goal origin, Goal updated) {
    boolean accumulatedChanged = !updated
      .getAccumulatedAmount()
      .getMajor()
      .equals(origin.getAccumulatedAmount().getMajor());
    boolean requiredChanged = !updated
      .getRequiredAmount()
      .getMajor()
      .equals(origin.getRequiredAmount().getMajor());
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
