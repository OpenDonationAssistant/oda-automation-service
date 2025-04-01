package io.github.opendonationassistant.automation.commands;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.dto.AutomationActionDto;
import io.github.opendonationassistant.automation.dto.AutomationDto;
import io.github.opendonationassistant.automation.dto.AutomationNumberVariableDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationStringVariableDto;
import io.github.opendonationassistant.automation.dto.AutomationTriggerDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationRuleData;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import java.util.Optional;

@Serdeable
public class SetStateCommand {

  private List<AutomationRuleDto> rules;
  private List<AutomationVariableDto> variables;

  public SetStateCommand(
    List<AutomationRuleDto> rules,
    List<AutomationVariableDto> variables
  ) {
    this.rules = rules;
    this.variables = variables;
  }

  public void execute(
    AutomationVariableRepository variablesRepository,
    AutomationRuleRepository rulesRepository,
    String recipientId
  ) {
    variables.forEach(variable -> {
      final Optional<AutomationVariable<?>> existing =
        variablesRepository.getById(recipientId, variable.getId());
      existing.ifPresentOrElse(
        it -> it.save(),
        () -> {
          switch (variable) {
            case AutomationNumberVariableDto it -> variablesRepository.create(
              recipientId,
              "number",
              it.getId(),
              it.getName(),
              String.valueOf(it.getValue())
            );
            case AutomationStringVariableDto it -> variablesRepository.create(
              recipientId,
              "string",
              it.getId(),
              it.getName(),
              it.getValue()
            );
            default -> {}
          }
        }
      );
    });
    rules.forEach(rule -> {
      final Optional<AutomationRule> existing =
        rulesRepository.getByRecipientIdAndRuleId(recipientId, rule.getId());
      existing.ifPresentOrElse(
        it -> it.save(),
        () -> {
          rulesRepository.create(
            recipientId,
            rule.getId(),
            rule.getName(),
            rule
              .getTriggers()
              .stream()
              .map(AutomationTriggerDto::asDomain)
              .toList(),
            rule
              .getActions()
              .stream()
              .map(AutomationActionDto::asDomain)
              .toList()
          );
        }
      );
    });
  }
}
