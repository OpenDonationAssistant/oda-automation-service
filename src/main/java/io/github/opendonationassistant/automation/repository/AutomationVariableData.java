package io.github.opendonationassistant.automation.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;

@MappedEntity("automationvariable")
@Serdeable
public class AutomationVariableData {

  @Id
  private String id;

  private String type;
  private String name;
  private String recipientId;
  private String value;

  public AutomationVariableData(
    String recipientId,
    String type,
    String id,
    String name,
    String value
  ) {
    this.recipientId = recipientId;
    this.type = type;
    this.id = id;
    this.name = name;
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public String getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getRecipientId() {
    return recipientId;
  }

  @Override
  public String toString() {
    try {
      return ObjectMapper.getDefault().writeValueAsString(this);
    } catch (Exception e) {
      return "Can't serialize as  json";
    }
  }
}
