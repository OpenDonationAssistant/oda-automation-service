package io.github.opendonationassistant.automation.listener;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.api.Widget;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.automation.domain.action.ActionFactory;
import io.github.opendonationassistant.automation.domain.trigger.TriggerFactory;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.goal.GoalSender;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.github.opendonationassistant.events.widget.WidgetProperty;
import io.github.opendonationassistant.events.widget.WidgetUpdateCommand;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
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

  private final WidgetCommandSender widgetCommandSender;

  @Inject
  public GoalListener(
    AutomationRuleRepository ruleRepository,
    TriggerFactory triggerFactory,
    ActionFactory actionFactory,
    GoalSender goalSender,
    WidgetsApi widgets,
    WidgetCommandSender widgetCommandSender
  ) {
    this.triggerFactory = triggerFactory;
    this.actionFactory = actionFactory;
    this.ruleRepository = ruleRepository;
    this.goalSender = goalSender;
    this.widgets = widgets;
    this.widgetCommandSender = widgetCommandSender;
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
    UpdatedGoal processedGoal = process(goal, rules);
    UpdatedGoal updatedGoal = processedGoal;
    while (checkForRepeat(updatedGoal, processedGoal)) {
      updatedGoal = processedGoal;
      processedGoal = process(updatedGoal, rules);
    }
    updateGoal(processedGoal);
  }

  private void updateGoal(UpdatedGoal goal) {
    final Widget widget = widgets.getWidget(goal.getWidgetId()).join();
    log.info("Updating widget {}", widget.getId());
    final Map<String, Object> config = widget.getConfig();

    final Stream<Map<String, Object>> existingGoals =
      ((List<Map<String, Object>>) config.get("properties")).stream()
        .filter(prop -> "goal".equals(prop.get("name")))
        .flatMap(existingGoal ->
          ((List<Map<String, Object>>) existingGoal.get("value")).stream()
        );

    final List<Map<String, Object>> updatedGoals = existingGoals
      .map(existingGoal -> {
        if (goal.getGoalId().equals(existingGoal.get("id"))) {
          existingGoal.put(
            "requiredAmount",
            Map.of(
              "major",
              goal.getRequiredAmount().getMajor(),
              "minor",
              goal.getRequiredAmount().getMinor(),
              "currency",
              "RUB"
            )
          );
        }
        return existingGoal;
      })
      .toList();
    var goals = new WidgetProperty();
    goals.setName("goal");
    goals.setValue(updatedGoals);
    log.info("widget patch: {}", updatedGoals);

    var patch = new WidgetConfig();
    patch.setProperties(List.of(goals));
    widgetCommandSender.send(new WidgetUpdateCommand(widget.getId(), patch));
  }

  private boolean checkForRepeat(UpdatedGoal origin, UpdatedGoal updated) {
    return (
      updated.getAccumulatedAmount().getMajor() !=
        origin.getAccumulatedAmount().getMajor() ||
      updated.getRequiredAmount().getMajor() !=
      origin.getRequiredAmount().getMajor()
    );
  }

  private UpdatedGoal process(UpdatedGoal goal, List<AutomationRule> rules) {
    var updatedGoal = new UpdatedGoal();
    updatedGoal.setGoalId(goal.getGoalId());
    updatedGoal.setWidgetId(goal.getWidgetId());
    updatedGoal.setIsDefault(goal.getIsDefault());
    updatedGoal.setRecipientId(goal.getRecipientId());
    updatedGoal.setRequiredAmount(
      new Amount(
        goal.getRequiredAmount().getMajor(),
        goal.getRequiredAmount().getMinor(),
        goal.getRequiredAmount().getCurrency()
      )
    );
    updatedGoal.setAccumulatedAmount(
      new Amount(
        goal.getAccumulatedAmount().getMajor(),
        goal.getAccumulatedAmount().getMinor(),
        goal.getAccumulatedAmount().getCurrency()
      )
    );
    updatedGoal.setFullDescription(goal.getFullDescription());
    updatedGoal.setBriefDescription(goal.getBriefDescription());
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
}
