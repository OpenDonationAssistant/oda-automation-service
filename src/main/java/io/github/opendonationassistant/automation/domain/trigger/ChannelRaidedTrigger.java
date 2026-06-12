package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.github.opendonationassistant.events.twitch.events.TwitchStreamStartedEvent;

public class ChannelRaidedTrigger extends AutomationTrigger {

  public ChannelRaidedTrigger(AutomationTriggerData data) {
    super(data);
  }

  @Override
  public boolean isTriggered(Object target) {
    return target instanceof TwitchStreamStartedEvent;
  }

  @Override
  public void extractVariables(Object target, Iteration iteration) {}
}
