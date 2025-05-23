package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.events.config.ConfigCommandSender;
import io.github.opendonationassistant.events.goal.GoalSender;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class ActionFactory {

  private WidgetCommandSender widgetCommandSender;
  private ConfigCommandSender configCommandSender;
  private WidgetsApi widgets;
  private AutomationVariableRepository variables;
  private GoalSender goalSender;

  @Inject
  public ActionFactory(
    WidgetsApi widgets,
    WidgetCommandSender widgetCommandSender,
    ConfigCommandSender configCommandSender,
    AutomationVariableRepository variables,
    GoalSender goalSender
  ) {
    this.widgetCommandSender = widgetCommandSender;
    this.configCommandSender = configCommandSender;
    this.widgets = widgets;
    this.variables = variables;
    this.goalSender = goalSender;
  }

  public AutomationAction create(
    String recipientId,
    String id,
    Map<String, Object> value,
    UpdatedGoal originGoal,
    UpdatedGoal updatedGoal
  ) {
    return switch (id) {
      case "increase-donation-goal" -> new IncreaseDonationGoalAction(
        id,
        value,
        widgets,
        widgetCommandSender,
        configCommandSender,
        updatedGoal
      );
      case "refresh-donation-goal" -> new RefreshDonationGoalAction(
        id,
        value,
        widgets,
        widgetCommandSender,
        configCommandSender,
        goalSender,
        updatedGoal
      );
      case "increase-variable" -> new IncreaseVariableAction(
        id,
        value,
        recipientId,
        variables
      );
      default -> new AutomationAction(id, value);
    };
  }
}
