package io.github.opendonationassistant.automation.domain;

import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.commons.logging.ODALogger;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Singleton
public class IterationFactory {

  private ODALogger log = new ODALogger(this);
  private final AutomationRuleRepository ruleRepository;
  private final AutomationVariableRepository variableRepository;
  private final AutomationMetrics metrics;

  @Inject
  public IterationFactory(
    AutomationRuleRepository ruleRepository,
    AutomationVariableRepository variableRepository,
    AutomationMetrics metrics
  ) {
    this.ruleRepository = ruleRepository;
    this.variableRepository = variableRepository;
    this.metrics = metrics;
  }

  public Iteration create(String recipientId, Object source) {
    var rules = ruleRepository
      .listByRecipientId(recipientId)
      .filter(it -> it.data().enabled())
      .toList();
    var variables = variableRepository.listByRecipientId(recipientId);
    var iteration = new Iteration(
      recipientId,
      source,
      variables,
      rules,
      metrics
    );
    final Supplier<Map<String, ?>> logSupplier = () -> {
      var output = new HashMap<String, Object>();
      output.put("recipientId", recipientId);
      output.put("rules", rules.stream().map(it -> it.data()).toList());
      output.put("variables", variables.stream().map(it -> it.data()).toList());
      return output;
    };
    log.debug("Created iteration", logSupplier);
    return iteration;
  }
}
