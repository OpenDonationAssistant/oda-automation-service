package io.github.opendonationassistant.automation.domain.trigger;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.EphemeralVariable;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.history.event.HistoryItemEvent;
import java.util.Optional;

public class DonationTrigger extends AutomationTrigger {

  public static final String ID = "donation";

  public DonationTrigger(AutomationTriggerData data) {
    super(data);
  }

  public Optional<String> getSystem() {
    return Optional.ofNullable((String) this.data().value().get("system"));
  }

  public int getMinAmount() {
    return intValue("minAmount").orElse(0);
  }

  public Optional<Integer> getMaxAmount() {
    return intValue("maxAmount");
  }

  @Override
  public boolean isTriggered(Object target) {
    if (!(target instanceof HistoryItemEvent event)) {
      return false;
    }
    if (!"payment".equals(event.type())) {
      return false;
    }
    if (
      !getSystem()
        .map(system -> system.equals(event.system()))
        .orElse(true)
    ) {
      return false;
    }
    Amount amount = event.amount();
    if (amount == null) {
      return false;
    }
    int major = amount.getMajor();
    return major >= getMinAmount() &&
      getMaxAmount().map(max -> major <= max).orElse(true);
  }

  private Optional<Integer> intValue(String name) {
    return Optional.ofNullable(this.data().value().get(name))
      .filter(Number.class::isInstance)
      .map(value -> ((Number) value).intValue());
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
      iteration.add(
        new EphemeralVariable<String>(
          "amount",
          String.valueOf(amount.getMajor())
        )
      );
      String currency = amount.getCurrency();
      if (currency != null && !currency.isBlank()) {
        iteration.add(new EphemeralVariable<String>("currency", currency));
      }
    }
    String message = event.message();
    if (message != null) {
      iteration.add(new EphemeralVariable<String>("message", message));
    }
  }
}
