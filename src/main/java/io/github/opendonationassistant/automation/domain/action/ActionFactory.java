package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationAction.EmptyAutomationAction;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
public class ActionFactory {

  private final AutomationVariableRepository variables;
  private final RabbitClient rabbit;
  private final HttpClient httpClient;

  @Inject
  public ActionFactory(
    AutomationVariableRepository variables,
    @Named("commands") RabbitClient rabbit,
    HttpClient httpClient
  ) {
    this.variables = variables;
    this.rabbit = rabbit;
    this.httpClient = httpClient;
  }

  public AutomationAction from(String recipientId, AutomationActionData data) {
    return switch (data.id()) {
      case IncreaseDonationGoalAction.ID -> new IncreaseDonationGoalAction(
        data
      );
      case RefreshDonationGoalAction.ID -> new RefreshDonationGoalAction(
        data
      );
      case IncreaseVariableAction.ID -> new IncreaseVariableAction(
        data,
        recipientId,
        variables
      );
      case RunReelAction.ID -> new RunReelAction(data, recipientId, rabbit);
      case PinTwitchMessageAction.ID -> new PinTwitchMessageAction(
        data,
        rabbit
      );
      case TwitchShoutoutAction.ID -> new TwitchShoutoutAction(data, rabbit);
      case TwitchAnnounceAction.ID -> new TwitchAnnounceAction(data, rabbit);
      case RestCallAction.ID -> new RestCallAction(data, httpClient);
      default -> new EmptyAutomationAction(data);
    };
  }
}
