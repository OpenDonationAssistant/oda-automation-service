package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.github.opendonationassistant.events.widget.WidgetProperty;
import io.github.opendonationassistant.events.widget.WidgetUpdateCommand;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshDonationGoalAction extends AutomationAction {

  private Logger log = LoggerFactory.getLogger(RefreshDonationGoalAction.class);

  private WidgetsApi widgets;
  private WidgetCommandSender widgetCommandSender;

  public RefreshDonationGoalAction(
    String id,
    Map<String, Object> value,
    WidgetsApi widgets,
    WidgetCommandSender widgetCommandSender
  ) {
    super(id, value);
    this.widgets = widgets;
    this.widgetCommandSender = widgetCommandSender;
  }

  public Optional<String> getWidgetId() {
    return Optional.ofNullable((String) this.getValue().get("widgetId"));
  }

  public Optional<String> getGoalId() {
    return Optional.ofNullable((String) this.getValue().get("goalId"));
  }

  public void execute() {
    log.info("Checking RefreshDonationGoalAction");
    getGoalId()
      .ifPresent(goalId ->
        getWidgetId()
          .map(widgets::getWidget)
          .ifPresent(widget -> {
            widget
              .thenAccept(it -> {
                log.info("Updating goal in widget {}", it.getId());
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
                widgetCommandSender.send(
                  new WidgetUpdateCommand(it.getId(), patch)
                );
              })
              .join(); // TODO: join?
          })
      );
  }
}
