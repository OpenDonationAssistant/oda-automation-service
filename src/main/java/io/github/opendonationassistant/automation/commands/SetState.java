package io.github.opendonationassistant.automation.commands;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.api.SetStateApi;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationRuleData;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@Validated
public class SetState extends BaseController implements SetStateApi {

  private final ODALogger log = new ODALogger(this);

  private AutomationVariableRepository variables;
  private AutomationRuleRepository rules;

  @Inject
  public SetState(
    AutomationVariableRepository variables,
    AutomationRuleRepository rules
  ) {
    this.variables = variables;
    this.rules = rules;
  }

  public HttpResponse<Void> setState(
    Authentication auth,
    @Valid @Body SetStateCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    log.info(
      "Processing SetState",
      Map.of("command", command, "recipientId", ownerId.get())
    );

    updateVariables(ownerId.get(), command);
    updateRules(ownerId.get(), command);
    return HttpResponse.ok();
  }

  private void updateVariables(String recipientId, SetStateCommand command) {
    final List<AutomationVariable<?>> existingVariables =
      variables.listByRecipientId(recipientId);
    existingVariables
      .stream()
      .forEach(existing -> {
        final Optional<AutomationVariableDto> variableToUpdate = command
          .variables()
          .stream()
          .filter(update -> update.id().equals(existing.data().id()))
          .findAny();
        variableToUpdate.ifPresentOrElse(
          newValue -> {
            if (existing instanceof AutomationStringVariable) {
              ((AutomationStringVariable) existing).update(
                  newValue.name(),
                  newValue.value()
                );
            }
            if (existing instanceof AutomationNumberVariable) {
              ((AutomationNumberVariable) existing).update(
                  newValue.name(),
                  new BigDecimal(newValue.value())
                );
            }
          },
          () -> existing.delete()
        );
      });

    command
      .variables()
      .stream()
      .filter(it ->
        existingVariables
          .stream()
          .filter(oldValue -> oldValue.data().id().equals(it.id()))
          .findAny()
          .isEmpty()
      )
      .forEach(valueToCreate ->
        variables.create(
          recipientId,
          valueToCreate.type(),
          valueToCreate.id(),
          valueToCreate.name(),
          valueToCreate.value()
        )
      );
  }

  private void updateRules(String recipientId, SetStateCommand command) {
    final List<AutomationRule> existingRules = rules.listByRecipientId(
      recipientId
    ).toList();
    existingRules
      .forEach(existing -> {
        final Optional<AutomationRuleDto> updatedRule = command
          .rules()
          .stream()
          .filter(it -> it.id().equals(existing.data().id()))
          .findAny();
        updatedRule.ifPresentOrElse(
          updated ->
            existing.update(
              new AutomationRuleData(
                updated.id(),
                updated.name(),
                recipientId,
                updated
                  .triggers()
                  .stream()
                  .map(trigger ->
                    new AutomationTriggerData(trigger.id(), trigger.value())
                  )
                  .toList(),
                updated
                  .actions()
                  .stream()
                  .map(action ->
                    new AutomationActionData(action.id(), action.value())
                  )
                  .toList(),
                existing.data().enabled()
              )
            ),
          () -> existing.delete()
        );
      });

    command
      .rules()
      .stream()
      .filter(rule ->
        existingRules
          .stream()
          .filter(it -> it.data().id().equals(rule.id()))
          .findAny()
          .isEmpty()
      )
      .forEach(rule ->
        rules.create(
          recipientId,
          rule.id(),
          rule.name(),
          rule
            .triggers()
            .stream()
            .map(trigger ->
              new AutomationTriggerData(trigger.id(), trigger.value())
            )
            .toList(),
          rule
            .actions()
            .stream()
            .map(action -> new AutomationActionData(action.id(), action.value())
            )
            .toList(),
          rule.enabled()
        )
      );
  }

  @Serdeable
  public static record SetStateCommand(
    @Valid @NotNull List<AutomationRuleDto> rules,
    @Valid @NotNull List<AutomationVariableDto> variables
  ) {}
}
