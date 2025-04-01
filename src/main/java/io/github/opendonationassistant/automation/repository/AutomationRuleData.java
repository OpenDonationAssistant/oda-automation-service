package io.github.opendonationassistant.automation.repository;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.AutomationTrigger;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import java.util.Map;

@MappedEntity("automationrule")
@Serdeable
public class AutomationRuleData {

  @Id
  private String id;

  private String name;

  private String recipientId;

  @MappedProperty(type = DataType.JSON)
  private List<AutomationTriggerData> triggers;

  @MappedProperty(type = DataType.JSON)
  private List<AutomationActionData> actions;

  public AutomationRuleData(
    String id,
    String name,
    String recipientId,
    List<AutomationTriggerData> triggers,
    List<AutomationActionData> actions
  ) {
    this.id = id;
    this.name = name;
    this.recipientId = recipientId;
    this.triggers = triggers;
    this.actions = actions;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<AutomationTriggerData> getTriggers() {
    return triggers;
  }

  public List<AutomationActionData> getActions() {
    return actions;
  }

  public AutomationRule asDomain(AutomationRuleDataRepository repository) {
    return new AutomationRule(
      this.getRecipientId(),
      this.getId(),
      this.getName(),
      this.getTriggers().stream().map(AutomationTriggerData::asDomain).toList(),
      this.getActions().stream().map(AutomationActionData::asDomain).toList(),
      repository
    );
  }

  @Serdeable
  public static class AutomationTriggerData {

    private String id;
    private Map<String, Object> value;

    public AutomationTriggerData(String id, Map<String, Object> value) {
      this.id = id;
      this.value = value;
    }

    public String getId() {
      return id;
    }

    public Map<String, Object> getValue() {
      return value;
    }

    public AutomationTrigger asDomain() {
      return new AutomationTrigger(this.getId(), this.getValue());
    }
  }

  @Serdeable
  public static class AutomationActionData {

    private String id;
    private Map<String, Object> value;

    public AutomationActionData(String id, Map<String, Object> value) {
      this.id = id;
      this.value = value;
    }

    public String getId() {
      return id;
    }

    public Map<String, Object> getValue() {
      return value;
    }

    public AutomationAction asDomain() {
      return new AutomationAction(this.getId(), this.getValue());
    }
  }
}
