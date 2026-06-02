package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationAction.EmptyAutomationAction;
import io.github.opendonationassistant.automation.domain.goal.Goal;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.rabbit.RabbitClient;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class ActionFactory {

  private final AutomationVariableRepository variables;
  private final RabbitClient rabbit;

  @Inject
  public ActionFactory(
    AutomationVariableRepository variables,
    @Named("commands") RabbitClient rabbit
  ) {
    this.variables = variables;
    this.rabbit = rabbit;
  }

  public AutomationAction convert(
    String recipientId,
    AutomationActionData data
  ) {
    return switch (data.id()) {
      case "increase-donation-goal" -> new IncreaseDonationGoalAction(data);
      case "refresh-donation-goal" -> new RefreshDonationGoalAction(data);
      case "increase-variable" -> new IncreaseVariableAction(
        data,
        recipientId,
        variables
      );
      case "run-reel" -> new RunReelAction(data, recipientId, rabbit);
      case "pin-twitch-message" -> new PinTwitchMessageAction(data, rabbit);
      case "twitch-shoutout" -> new TwitchShoutoutAction(data, rabbit);
      default -> new EmptyAutomationAction(data);
    };
  }

  public AutomationAction from(String recipientId, AutomationActionData data) {
    return switch (data.id()) {
      case "increase-donation-goal" -> new IncreaseDonationGoalAction(data);
      case "refresh-donation-goal" -> new RefreshDonationGoalAction(data);
      case "increase-variable" -> new IncreaseVariableAction(
        data,
        recipientId,
        variables
      );
      case "run-reel" -> new RunReelAction(data, recipientId, rabbit);
      case "pin-twitch-message" -> new PinTwitchMessageAction(data, rabbit);
      case "twitch-shoutout" -> new TwitchShoutoutAction(data, rabbit);
      default -> new EmptyAutomationAction(data);
    };
  }
}
