package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class ActionFactory {

  private WidgetCommandSender widgetCommandSender;
  private WidgetsApi widgets;

  @Inject
  public ActionFactory(
    WidgetsApi widgets,
    WidgetCommandSender widgetCommandSender
  ) {
    this.widgetCommandSender = widgetCommandSender;
    this.widgets = widgets;
  }

  public AutomationAction create(String id, Map<String, Object> value) {
    return switch (id) {
      case "increase-donation-goal" -> new IncreaseDonationGoalAction(id, value, widgets, widgetCommandSender);
      case "refresh-donation-goal" -> new RefreshDonationGoalAction(
        id,
        value,
        widgets,
        widgetCommandSender
      );
      case "increase-variable" -> new AutomationAction(id, value);
      default -> new AutomationAction(id, value);
    };
  }
}
