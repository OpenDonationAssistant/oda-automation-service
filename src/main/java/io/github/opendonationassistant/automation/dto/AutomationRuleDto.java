package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.repository.AutomationRuleDataRepository;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record AutomationRuleDto(
  String id,
  String name,
  List<AutomationTriggerDto> triggers,
  List<AutomationActionDto> actions
) {
  public AutomationRule asDomain(
    String recipientId,
    AutomationRuleDataRepository repository
  ) {
    return new AutomationRule(
      recipientId,
      id,
      name,
      triggers.stream().map(AutomationTriggerDto::asDomain).toList(),
      actions.stream().map(AutomationActionDto::asDomain).toList(),
      repository
    );
  }
}
