package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.events.config.ConfigCommandSender;
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

  @Inject
  public ActionFactory(
    WidgetsApi widgets,
    WidgetCommandSender widgetCommandSender,
    ConfigCommandSender configCommandSender,
    AutomationVariableRepository variables
  ) {
    this.widgetCommandSender = widgetCommandSender;
    this.configCommandSender = configCommandSender;
    this.widgets = widgets;
    this.variables = variables;
  }

  public AutomationAction create(
    String recipientId,
    String id,
    Map<String, Object> value
  ) {
    return switch (id) {
      case "increase-donation-goal" -> new IncreaseDonationGoalAction(
        id,
        value,
        widgets,
        widgetCommandSender
      );
      case "refresh-donation-goal" -> new RefreshDonationGoalAction(
        id,
        value,
        widgets,
        widgetCommandSender,
        configCommandSender
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
