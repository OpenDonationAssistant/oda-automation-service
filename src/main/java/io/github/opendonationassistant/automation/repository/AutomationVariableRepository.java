package io.github.opendonationassistant.automation.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.micronaut.core.util.StringUtils;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
public class AutomationVariableRepository {

  private final ODALogger log = new ODALogger(this);
  private AutomationVariableDataRepository repository;

  @Inject
  public AutomationVariableRepository(
    AutomationVariableDataRepository repository
  ) {
    this.repository = repository;
  }

  public List<AutomationVariable<?>> listByRecipientId(String recipientId) {
    return repository
      .getByRecipientId(recipientId)
      .stream()
      .map(it -> this.convert(it))
      .reduce(
        new ArrayList<AutomationVariable<?>>(),
        (list, variable) -> {
          list.add(variable);
          return list;
        },
        (list1, list2) -> {
          list1.addAll(list2);
          return list1;
        }
      );
  }

  public Optional<AutomationVariable<?>> getById(
    String recipientId,
    String id
  ) {
    return repository.getByRecipientIdAndId(recipientId, id).map(this::convert);
  }

  public AutomationVariable<?> update(
    String recipientId,
    String type,
    String variableId,
    String variableName,
    String variableValue
  ) {
    var data = new AutomationVariableData(
      variableId,
      type,
      Optional.ofNullable(variableName).orElse("<Без названия>"),
      recipientId,
      Optional.ofNullable(variableValue).orElse("")
    );
    repository.update(data);
    final AutomationVariable<?> created = convert(data);
    log.debug("Updated variable", Map.of("variable", created));
    return created;
  }

  public AutomationVariable<?> create(
    String recipientId,
    String type,
    @Nullable String variableId,
    @Nullable String variableName,
    @Nullable String variableValue
  ) {
    var id = Optional.ofNullable(variableId).orElseGet(() ->
      Generators.timeBasedEpochGenerator().generate().toString()
    );
    var data = new AutomationVariableData(
      id,
      type,
      Optional.ofNullable(variableName).orElse("<Без названия>"),
      recipientId,
      Optional.ofNullable(variableValue).orElse("")
    );
    repository.save(data);
    final AutomationVariable<?> created = convert(data);
    log.debug("Created variable", Map.of("variable", created));
    return created;
  }

  private AutomationVariable<?> convert(AutomationVariableData it) {
    return switch (it.type()) {
      case "number" -> new AutomationNumberVariable(
        it.recipientId(),
        it.id(),
        it.name(),
        Optional.ofNullable(it.value())
          .filter(StringUtils::isNotEmpty)
          .map(BigDecimal::new)
          .orElse(BigDecimal.ZERO),
        repository
      );
      case "string" -> new AutomationStringVariable(
        it.recipientId(),
        it.id(),
        it.name(),
        it.value(),
        repository
      );
      default -> new AutomationVariable<String>(
        it.recipientId(),
        it.id(),
        it.name(),
        it.value(),
        repository
      );
    };
  }

}
