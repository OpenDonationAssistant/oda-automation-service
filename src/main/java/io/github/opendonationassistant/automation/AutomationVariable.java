package io.github.opendonationassistant.automation;

import io.github.opendonationassistant.automation.repository.AutomationVariableData;
import io.github.opendonationassistant.automation.repository.AutomationVariableDataRepository;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.HasRecipientId;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.serde.annotation.Serdeable;
import java.io.IOException;
import java.util.Map;

public class AutomationVariable<T> implements IVariable<T> {

  private static final ODALogger log = new ODALogger(AutomationVariable.class);

  private AutomationVariableData data;
  private final AutomationVariableDataRepository repository;
  private final RabbitClient rabbit;

  public AutomationVariable(
    AutomationVariableData data,
    AutomationVariableDataRepository repository,
    RabbitClient rabbit
  ) {
    this.data = data;
    this.repository = repository;
    this.rabbit = rabbit;
  }

  public AutomationVariableData data() {
    return data;
  }

  public String name() {
    return data.name();
  }

  public T value() {
    return (T) data.value();
  }

  public void update(String name, T value) {
    update(
      new AutomationVariableData(
        data.id(),
        data.type(),
        name,
        data.recipientId(),
        value.toString()
      )
    );
  }

  protected void update(AutomationVariableData data) {
    this.data = data;
    this.save();
  }

  public void save() {
    if (
      repository
        .getByRecipientIdAndId(data.recipientId(), data.id())
        .isPresent()
    ) {
      log.debug("Updating variable", Map.of("updated", data));
      repository.update(data);
      send(
        new VariableUpdated(
          data.id(),
          data.recipientId(),
          data.name(),
          data.type(),
          data.value()
        )
      );
    } else {
      log.debug("Creating variable", Map.of("created", data));
      repository.save(data);
      send(
        new VariableCreated(
          data.id(),
          data.recipientId(),
          data.name(),
          data.type(),
          data.value()
        )
      );
    }
  }

  private void send(HasRecipientId event) {
    try {
      rabbit.sendEvent(event);
    } catch (IOException e) {
      log.error("Failed to send variable event", e);
      throw new RuntimeException(e);
    }
  }

  public void delete() {
    repository
      .getByRecipientIdAndId(data.recipientId(), data.id())
      .ifPresent(existing -> {
        repository.deleteById(existing.id());
        send(
          new VariableDeleted(
            existing.id(),
            existing.recipientId(),
            existing.name(),
            existing.type(),
            existing.value()
          )
        );
      });
  }

  public static enum Scope {
    GLOBAL,
    RULE,
  }

  public sealed interface VariableEvent extends HasRecipientId
    permits VariableCreated, VariableUpdated {
    String id();
    String name();
    String type();
    String value();
  }

  @Serdeable
  public record VariableCreated(
    String id,
    String recipientId,
    String name,
    String type,
    String value
  ) implements VariableEvent {}

  @Serdeable
  public record VariableUpdated(
    String id,
    String recipientId,
    String name,
    String type,
    String value
  ) implements VariableEvent {}

  @Serdeable
  public record VariableDeleted(
    String id,
    String recipientId,
    String name,
    String type,
    String value
  ) implements HasRecipientId {}
}
