package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.dto.AutomationActionDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationTriggerDto;
import io.github.opendonationassistant.automation.repository.AutomationRuleData;
import io.github.opendonationassistant.automation.repository.AutomationRuleDataRepository;
import io.github.opendonationassistant.commons.ToString;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.util.List;

@Serdeable
@EqualsAndHashCode
public class AutomationRule {

  private String id;
  private String name;
  private String recipientId;
  private List<AutomationTrigger> triggers;
  private List<AutomationAction> actions;
  private AutomationRuleDataRepository repository;

  public AutomationRule(
    String recipientId,
    String id,
    String name,
    List<AutomationTrigger> triggers,
    List<AutomationAction> actions,
    AutomationRuleDataRepository repository
  ) {
    this.recipientId = recipientId;
    this.id = id;
    this.name = name;
    this.triggers = triggers;
    this.actions = actions;
    this.repository = repository;
  }

  public void save() {
    repository.update(
      new AutomationRuleData(
        this.getId(),
        this.getName(),
        this.getRecipientId(),
        this.getTriggers().stream().map(AutomationTrigger::asData).toList(),
        this.getActions().stream().map(AutomationAction::asData).toList()
      )
    );
  }

  public void delete() {
    repository.deleteById(this.getId());
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<AutomationTrigger> getTriggers() {
    return triggers;
  }

  public void setTriggers(List<AutomationTrigger> triggers) {
    this.triggers = triggers;
  }

  public List<AutomationAction> getActions() {
    return actions;
  }

  public void setActions(List<AutomationAction> actions) {
    this.actions = actions;
  }

  public AutomationRuleDto asDto() {
    return new AutomationRuleDto(
      this.getId(),
      this.getName(),
      this.getTriggers()
        .stream()
        .map(trigger ->
          new AutomationTriggerDto(trigger.getId(), trigger.getValue())
        )
        .toList(),
      this.getActions()
        .stream()
        .map(action ->
          new AutomationActionDto(action.getId(), action.getValue())
        )
        .toList()
    );
  }

  @Override
  public String toString() {
    return ToString.asJson(this);
  }

  @Override
  public boolean equals(Object o) {
    return AutomationRuleObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationRuleObject.hashCode(this);
  }

  public String getRecipientId() {
    return recipientId;
  }
}
