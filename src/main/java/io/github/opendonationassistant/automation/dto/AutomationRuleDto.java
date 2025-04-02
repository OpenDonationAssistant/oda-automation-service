package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.repository.AutomationRuleDataRepository;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.util.List;

@Serdeable
@EqualsAndHashCode
public class AutomationRuleDto {

  private String id;
  private String name;
  private List<AutomationTriggerDto> triggers;
  private List<AutomationActionDto> actions;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<AutomationTriggerDto> getTriggers() {
    return triggers;
  }

  public List<AutomationActionDto> getActions() {
    return actions;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTriggers(List<AutomationTriggerDto> triggers) {
    this.triggers = triggers;
  }

  public void setActions(List<AutomationActionDto> actions) {
    this.actions = actions;
  }

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

  @Override
  public String toString() {
    try {
      return ObjectMapper.getDefault().writeValueAsString(this);
    } catch (Exception e) {
      return "Can't serialize as  json: " + e.getMessage();
    }
  }

  @Override
  public boolean equals(Object o) {
    return AutomationRuleDtoObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationRuleDtoObject.hashCode(this);
  }
}
