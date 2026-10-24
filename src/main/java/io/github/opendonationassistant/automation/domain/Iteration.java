package io.github.opendonationassistant.automation.domain;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.IVariable;
import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.commons.logging.ODALogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class Iteration {

  private ODALogger log = new ODALogger(this);
  private final String recipientId;
  private final Object source;
  private final List<IVariable<?>> variables;
  private final List<AutomationRule> rules;
  private final AutomationMetrics metrics;

  public Iteration(
    String recipientId,
    Object source,
    List<AutomationVariable<?>> variables,
    List<AutomationRule> rules,
    AutomationMetrics metrics
  ) {
    this.recipientId = recipientId;
    this.source = source;
    this.variables = new ArrayList<>();
    this.variables.addAll(variables);
    this.rules = rules;
    this.metrics = metrics;
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

  /**
   * Resolves a variable value, preferring the provided overrides (for example
   * values configured on an action) over the variables extracted by triggers.
   */
  public Optional<Object> resolveVariable(
    String name,
    Map<String, ?> overrides
  ) {
    var configured = overrides.get(name);
    if (configured != null) {
      return Optional.of(configured);
    }
    return variable(name)
      .map(resolved -> (Object) resolved.value())
      .filter(Objects::nonNull);
  }

  public void run() {
    final Supplier<Map<String, ?>> logSupplier = () -> {
      var output = new HashMap<String, Object>();
      output.put("recipientId", recipientId);
      output.put("source", source);
      output.put(
        "triggers",
        rules
          .stream()
          .map(it ->
            it
              .getTriggers()
              .stream()
              .map(trigger -> trigger.getClass().getCanonicalName())
          )
          .toList()
      );
      return output;
    };
    log.debug("Running iteration", logSupplier);
    metrics.iterationRun(source);
    rules.forEach(rule ->
      rule
        .getTriggers()
        .stream()
        .filter(trigger -> trigger.isTriggered(source))
        .findAny()
        .ifPresent(trigger -> {
          log.debug("Trigger found", Map.of("id", trigger.data().id()));
          trigger.extractVariables(source, this);
          rule.getActions().forEach(action -> action.execute(this));
        })
    );
  }
}
