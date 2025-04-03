package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.events.config.ConfigCommandSender;
import io.github.opendonationassistant.events.config.ConfigPutCommand;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.github.opendonationassistant.events.widget.WidgetProperty;
import io.github.opendonationassistant.events.widget.WidgetUpdateCommand;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncreaseDonationGoalAction extends AutomationAction {

  private Logger log = LoggerFactory.getLogger(
    IncreaseDonationGoalAction.class
  );

  private WidgetsApi widgets;
  private WidgetCommandSender widgetCommandSender;
  private ConfigCommandSender configCommandSender;

  public IncreaseDonationGoalAction(
    String id,
    Map<String, Object> value,
    WidgetsApi widgets,
    WidgetCommandSender widgetCommandSender,
    ConfigCommandSender configCommandSender
  ) {
    super(id, value);
    this.widgets = widgets;
    this.widgetCommandSender = widgetCommandSender;
    this.configCommandSender = configCommandSender;
  }

  public Optional<Integer> getIncreaseAmount() {
    return Optional.ofNullable((Integer) this.getValue().get("amount"));
  }

  public Optional<String> getWidgetId() {
    return Optional.ofNullable((String) this.getValue().get("widgetId"));
  }

  public Optional<String> getGoalId() {
    return Optional.ofNullable((String) this.getValue().get("goalId"));
  }

  public void execute() {
    log.info("Checking IncreaseDonationGoalAction: {}", getWidgetId());
    getWidgetId()
      .map(widgets::getWidget)
      .ifPresent(widget -> {
        widget
          .thenAccept(it -> {
            log.info("Updating amount in goal in widget {}", it.getId());
            final Map<String, Object> config = it.getConfig();
            final Stream<Map<String, Object>> existingGoals =
              ((List<Map<String, Object>>) config.get("properties")).stream()
                .filter(prop -> "goal".equals(prop.get("name")))
                .flatMap(goal ->
                  ((List<Map<String, Object>>) goal.get("value")).stream()
                );
            final List<Map<String, Object>> updatedGoals = existingGoals
              .map(goal -> {
                if (
                  getGoalId()
                    .filter(id -> id.equals(goal.get("id")))
                    .isPresent()
                ) {
                  final Map<String, Object> required =
                    ((Map<String, Object>) goal.getOrDefault(
                        "requiredAmount",
                        Map.of()
                      ));
                  Integer totalAmount = (Integer) required.getOrDefault(
                    "major",
                    0
                  );
                  goal.put(
                    "requiredAmount",
                    Map.of(
                      "major",
                      totalAmount + getIncreaseAmount().orElse(0),
                      "currency",
                      "RUB"
                    )
                  );
                }
                return goal;
              })
              .toList();
            var goals = new WidgetProperty();
            goals.setName("goal");
            goals.setValue(updatedGoals);

            var patch = new WidgetConfig();
            patch.setProperties(List.of(goals));
            widgetCommandSender.send(
              new WidgetUpdateCommand(it.getId(), patch)
            );

            var command = new ConfigPutCommand();
            command.setKey("goals");
            command.setValue(updatedGoals);
            command.setOwnerId(it.getOwnerId());
            command.setName("paymentpage");
            configCommandSender.send(command);
          })
          .join(); // TODO: join?
      });
  }
}
