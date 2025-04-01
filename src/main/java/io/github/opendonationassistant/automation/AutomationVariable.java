package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.EqualsAndHashCode;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Serdeable
@EqualsAndHashCode
public class AutomationVariable<T> {

  private Logger log = LoggerFactory.getLogger(AutomationVariable.class);

  private String recipientId;
  private String id;
  private String name;
  private T value;
  private AutomationVariableDataRepository repository;

  public AutomationVariable(
    String recipientId,
    String id,
    String name,
    AutomationVariableDataRepository repository
  ) {
    this.recipientId = recipientId;
    this.id = id;
    this.repository = repository;
    this.name = Optional.ofNullable(name).orElse("<Без названия>");
  }

  protected AutomationVariableData extractData() {
    return new AutomationVariableData(
      this.getId(),
      "undefined",
      this.getRecipientId(),
      this.getName(),
      String.valueOf(this.getValue())
    );
  }

  public void save() {
    final AutomationVariableData updated = extractData();
    log.debug("updating variable to {}", updated);
    repository.update(updated);
  }

  public void delete() {
    repository.deleteById(this.getId());
  }

  public AutomationVariableDto asDto() {
    return new AutomationVariableDto(this.getId(), this.getName()) {
      @Override
      public AutomationVariable<?> asDomain(
        String recipientId,
        AutomationVariableDataRepository repository
      ) {
        return new AutomationVariable<>(
          recipientId,
          this.getId(),
          this.getName(),
          repository
        );
      }
    };
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

  @Override
  public boolean equals(Object o) {
    return AutomationVariableObject.equals(this, o);
  }

  @Override
  public int hashCode() {
    return AutomationVariableObject.hashCode(this);
  }
}
