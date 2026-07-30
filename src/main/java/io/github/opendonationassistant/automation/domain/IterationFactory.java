package io.github.opendonationassistant.automation.domain;

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

  @Inject
  public IterationFactory(
    AutomationRuleRepository ruleRepository,
    AutomationVariableRepository variableRepository
  ) {
    this.ruleRepository = ruleRepository;
    this.variableRepository = variableRepository;
  }

  public Iteration create(String recipientId, Object source) {
    var iteration = new Iteration(
      recipientId,
      source,
      variableRepository.listByRecipientId(recipientId),
      ruleRepository.listByRecipientId(recipientId)
    );
    final Supplier<Map<String, ?>> supplier = () -> {
      var output = new HashMap<String, Object>();
      output.put("recipientId", recipientId);
      return output;
    };
    log.debug("Created iteration", supplier);
    return iteration;
  }
}
