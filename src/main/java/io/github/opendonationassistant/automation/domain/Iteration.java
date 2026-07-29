package io.github.opendonationassistant.automation.domain;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.IVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Iteration {

  private final String recipientId;
  private final Object source;
  private final List<IVariable<?>> variables;
  private final List<AutomationRule> rules;

  public Iteration(
    String recipientId,
    Object source,
    List<AutomationVariable<?>> variables,
    List<AutomationRule> rules
  ) {
    this.recipientId = recipientId;
    this.source = source;
    this.variables = new ArrayList<>();
    this.variables.addAll(variables);
    this.rules = rules;
  }

  public String recipientId() {
    return recipientId;
  }

  public Object source() {
    return source;
  }

  public void add(IVariable<?> variable) {
    this.variables.add(variable);
  }

  public Optional<IVariable<?>> variable(String name) {
    return variables
      .stream()
      .filter(variable -> variable.name().equals(name))
      .findAny();
  }

  public void run() {
    rules.forEach(rule ->
      rule
        .getTriggers()
        .stream()
        .filter(trigger -> trigger.isTriggered(source))
        .findAny()
        .ifPresent(trigger -> {
          trigger.extractVariables(source, this);
          rule.getActions().forEach(action -> action.execute(this));
        })
    );
  }
}
