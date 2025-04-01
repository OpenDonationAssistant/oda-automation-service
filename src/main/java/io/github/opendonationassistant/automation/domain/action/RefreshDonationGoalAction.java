package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.github.opendonationassistant.events.widget.WidgetProperty;
import io.github.opendonationassistant.events.widget.WidgetUpdateCommand;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RefreshDonationGoalAction extends AutomationAction {

  private WidgetsApi widgets;
  private String widgetId;
  private String goalId;

  public RefreshDonationGoalAction(String id, Map<String, Object> value) {
    super(id, value);
  }

  public Optional<String> getWidgetId() {
    return Optional.ofNullable((String) this.getValue().get("widgetId"));
  }

  public Optional<String> getGoalId() {
    return Optional.ofNullable((String) this.getValue().get("goalId"));
  }

  public void execute() {
    getWidgetId()
      .map(widgets::getWidget)
      .ifPresent(widget -> {
        widget
          .thenAccept(it -> {
            final Map<String, Object> config = it.getConfig();
            var goals = new WidgetProperty();
            goals.setName("goal");
            goals.setValue(
              ((List<Map<String, Object>>) config.get("goal")).stream()
                .map(goal -> {
                  if (goalId.equals(goal.get("id"))) {
                    goal.put(
                      "accumulatedAmount",
                      Map.of("major", 0, "currency", "RUB")
                    );
                  }
                  return goal;
                })
                .toList()
            );

            var patch = new WidgetConfig();
            patch.setProperties(List.of(goals));
            new WidgetUpdateCommand(it.getId(), patch);
          })
          .join(); // TODO: join?
      });
  }
}
