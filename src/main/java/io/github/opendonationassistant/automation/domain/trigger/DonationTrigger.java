package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.EphemeralVariable;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.history.event.HistoryItemEvent;

public class DonationTrigger extends AutomationTrigger {

  public static final String ID = "donation";

  public DonationTrigger(AutomationTriggerData data) {
    super(data);
  }

  @Override
  public boolean isTriggered(Object target) {
    return target instanceof HistoryItemEvent;
  }

  @Override
  public void extractVariables(Object target, Iteration iteration) {
    if (!(target instanceof HistoryItemEvent event)) {
      return;
    }
    String nickname = event.nickname();
    if (nickname != null) {
      iteration.add(new EphemeralVariable<String>("nickname", nickname));
    }
    Amount amount = event.amount();
    if (amount != null) {
      String currency = amount.getCurrency();
      String renderedAmount =
        currency != null && !currency.isBlank()
          ? amount.getMajor() + " " + currency
          : String.valueOf(amount.getMajor());
      iteration.add(new EphemeralVariable<String>("amount", renderedAmount));
    }
    String message = event.message();
    if (message != null) {
      iteration.add(new EphemeralVariable<String>("message", message));
    }
  }
}
