package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.api.Widget;
import io.github.opendonationassistant.automation.api.WidgetsApi;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.config.ConfigCommandSender;
import io.github.opendonationassistant.events.config.ConfigPutCommand;
import io.github.opendonationassistant.events.goal.GoalSender;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
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

public class RefreshDonationGoalAction extends AutomationAction {

  private Logger log = LoggerFactory.getLogger(RefreshDonationGoalAction.class);

  private WidgetsApi widgets;
  private WidgetCommandSender widgetCommandSender;
  private ConfigCommandSender configCommandSender;
  private GoalSender goalSender;
  private final Optional<Widget> widget;

  public RefreshDonationGoalAction(
    String id,
    Map<String, Object> value,
    WidgetsApi widgets,
    WidgetCommandSender widgetCommandSender,
    ConfigCommandSender configCommandSender,
    GoalSender goalSender
  ) {
    super(id, value);
    this.widgets = widgets;
    this.widgetCommandSender = widgetCommandSender;
    this.configCommandSender = configCommandSender;
    this.goalSender = goalSender;
    this.widget = getWidgetId()
      .map(widgetId -> widgets.getWidget(widgetId).join());
  }

  public Optional<String> getWidgetId() {
    return Optional.ofNullable((String) this.getValue().get("widgetId"));
  }

  public Optional<String> getGoalId() {
    return Optional.ofNullable((String) this.getValue().get("goalId"));
  }

  private Integer getActualRequired() {
    var goalId = getGoalId().get();
    final Widget actualWidget = getWidgetId()
      .map(widgetId -> widgets.getWidget(widgetId).join())
      .get();
    final Map<String, Object> config = actualWidget.getConfig();
    final Stream<Map<String, Object>> existingGoals =
      ((List<Map<String, Object>>) config.get("properties")).stream()
        .filter(prop -> "goal".equals(prop.get("name")))
        .flatMap(goal ->
          ((List<Map<String, Object>>) goal.get("value")).stream()
        );
    return existingGoals
      .filter(goal -> goalId.equals(goal.get("id")))
      .map(goal -> {
        final Map<String, Object> required =
          ((Map<String, Object>) goal.getOrDefault("requiredAmount", Map.of()));
        Integer requiredAmount = (Integer) required.getOrDefault("major", 0);
        return requiredAmount;
      })
      .findAny().get();
  }

  public void execute() {
    log.info("Checking RefreshDonationGoalAction");
    getGoalId()
      .ifPresent(goalId ->
        widget.ifPresent(it -> {
          log.info("Updating goal in widget {}", it.getId());
          final Map<String, Object> config = it.getConfig();
          final Stream<Map<String, Object>> existingGoals =
            ((List<Map<String, Object>>) config.get("properties")).stream()
              .filter(prop -> "goal".equals(prop.get("name")))
              .flatMap(goal ->
                ((List<Map<String, Object>>) goal.get("value")).stream()
              );
          final List<Map<String, Object>> updatedGoals = existingGoals
            .map(goal -> {
              if (goalId.equals(goal.get("id"))) {
                final Map<String, Object> required =
                  ((Map<String, Object>) goal.getOrDefault(
                      "requiredAmount",
                      Map.of()
                    ));
                Integer requiredAmount = (Integer) required.getOrDefault(
                  "major",
                  0
                );
                goal.put("requiredAmount",Map.of("major", getActualRequired(), "minor", 0, "currency", "RUB"));
                final Map<String, Object> collected =
                  ((Map<String, Object>) goal.getOrDefault(
                      "accumulatedAmount",
                      Map.of()
                    ));
                Integer collectedAmount = (Integer) collected.getOrDefault(
                  "major",
                  0
                );
                int diff = collectedAmount - requiredAmount;
                log.info(
                  "changing collected amount, previous: {}, required: {}, diff: {}",
                  collectedAmount,
                  requiredAmount,
                  diff
                );
                goal.put(
                  "accumulatedAmount",
                  Map.of("major", diff > 0 ? diff : 0, "currency", "RUB")
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
          widgetCommandSender.send(new WidgetUpdateCommand(it.getId(), patch));

          // TODO: make one flow
          var command = new ConfigPutCommand();
          command.setKey("goals");
          command.setValue(updatedGoals);
          command.setOwnerId(it.getOwnerId());
          command.setName("paymentpage");
        })
      );
  }
}
