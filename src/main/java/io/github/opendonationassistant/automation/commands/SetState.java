package io.github.opendonationassistant.automation.commands;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.dto.AutomationActionDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationTriggerDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationRuleDataRepository;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class SetState extends BaseController {

  private Logger log = LoggerFactory.getLogger(SetState.class);

  private AutomationVariableRepository variables;
  private AutomationRuleRepository rules;
  private AutomationRuleDataRepository ruleDataRepository;
  private AutomationVariableDataRepository variableDataRepository;

  @Inject
  public SetState(
    AutomationVariableRepository variables,
    AutomationRuleRepository rules,
    AutomationRuleDataRepository ruleDataRepository,
    AutomationVariableDataRepository variableDataRepository
  ) {
    this.variables = variables;
    this.rules = rules;
    this.variableDataRepository = variableDataRepository;
    this.ruleDataRepository = ruleDataRepository;
  }

  @Post("/automation/commands/setstate")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> setState(
    Authentication auth,
    @Body SetStateCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    MDC.put(
      "context",
      ToString.asJson(Map.of("command", command, "recipientId", ownerId.get()))
    );
    log.info("Processing SetState");

    updateVariables(ownerId.get(), command);
    updateRules(ownerId.get(), command);
    return HttpResponse.ok();
  }

  private void updateVariables(String recipientId, SetStateCommand command) {
    final List<AutomationVariable<?>> existingVariables =
      variables.listByRecipientId(recipientId);
    existingVariables
      .stream()
      .forEach(it -> {
        final Optional<AutomationVariableDto> updatedVariable = command
          .variables()
          .stream()
          .filter(updated -> updated.getId().equals(it.getId()))
          .findAny();
        updatedVariable.ifPresentOrElse(
          newValue -> {
            newValue.asDomain(recipientId, variableDataRepository).save();
          },
          () -> it.delete()
        );
      });
    command
      .variables()
      .stream()
      .filter(it ->
        existingVariables
          .stream()
          .filter(oldValue -> oldValue.getId().equals(it.getId()))
          .findAny()
          .isEmpty()
      )
      .forEach(valueToCreate -> {
        switch (valueToCreate.getType()) {
          case "string" -> variables.create(
            recipientId,
            "string",
            valueToCreate.getId(),
            valueToCreate.getName(),
            valueToCreate.getValue()
          );
          case "number" -> variables.create(
            recipientId,
            "number",
            valueToCreate.getId(),
            valueToCreate.getName(),
            valueToCreate.getValue()
          );
          default -> {}
        }
      });
  }

  private void updateRules(String recipientId, SetStateCommand command) {
    final List<AutomationRule> existingRules = rules.listByRecipientId(
      recipientId
    );
    existingRules
      .stream()
      .forEach(rule -> {
        final Optional<AutomationRuleDto> updatedRule = command
          .rules()
          .stream()
          .filter(it -> it.getId().equals(rule.getId()))
          .findAny();
        updatedRule.ifPresentOrElse(
          updated -> updated.asDomain(recipientId, ruleDataRepository).save(),
          () -> rule.delete()
        );
      });
    command
      .rules()
      .stream()
      .filter(rule ->
        existingRules
          .stream()
          .filter(it -> it.getId().equals(rule.getId()))
          .findAny()
          .isEmpty()
      )
      .forEach(rule ->
        rules.create(
          recipientId,
          rule.getId(),
          rule.getName(),
          rule
            .getTriggers()
            .stream()
            .map(AutomationTriggerDto::asDomain)
            .toList(),
          rule.getActions().stream().map(AutomationActionDto::asDomain).toList()
        )
      );
  }

  @Serdeable
  public static record SetStateCommand(
    List<AutomationRuleDto> rules,
    List<AutomationVariableDto> variables
  ) {}
}
