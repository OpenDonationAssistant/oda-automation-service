package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.micronaut.serde.ObjectMapper;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;

import java.util.Map;
import java.util.Optional;

@Serdeable
@EqualsAndHashCode
public class AutomationVariable<T> {

  private final ODALogger log = new ODALogger(this);

  private String recipientId;
  private String id;
  private String name;
  private T value;
  private AutomationVariableDataRepository repository;

  public AutomationVariable(
    String recipientId,
    String id,
    String name,
    T value,
    AutomationVariableDataRepository repository
  ) {
    this.recipientId = recipientId;
    this.id = id;
    this.repository = repository;
    this.value = value;
    this.name = Optional.ofNullable(name).orElse("<Без названия>");
  }

  protected AutomationVariableData extractData() {
    return new AutomationVariableData(
      this.getId(),
      "undefined",
      this.getName(),
      this.getRecipientId(),
      String.valueOf(this.getValue())
    );
  }

  public void save() {
    final AutomationVariableData updated = extractData();
    log.debug("Updating variable", Map.of("updated", updated));
    repository.update(updated);
  }

  public void delete() {
    repository.deleteById(this.getId());
  }

  public AutomationVariableDto asDto() {
    return new AutomationVariableDto(
      this.id,
      this.name,
      "string",
      String.valueOf(this.value)
    );
  }

  @Override
  public String toString() {
    try {
      return ObjectMapper.getDefault().writeValueAsString(this);
    } catch (Exception e) {
      return "Can't serialize as  json";
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }
}
