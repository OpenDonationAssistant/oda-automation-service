package io.github.opendonationassistant.automation.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationNumberVariable;
import io.github.opendonationassistant.automation.domain.variable.AutomationStringVariable;
import io.micronaut.core.util.StringUtils;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AutomationVariableRepository {

  private Logger log = LoggerFactory.getLogger(
    AutomationVariableRepository.class
  );
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
      recipientId,
      type,
      variableId,
      Optional.ofNullable(variableName).orElse("<Без названия>"),
      Optional.ofNullable(variableValue).orElse("")
    );
    final Optional<AutomationVariableData> existing =
      repository.getByRecipientIdAndId(recipientId, variableId);
    if (existing.isEmpty()) {}
    repository.save(data);
    final AutomationVariable<?> created = convert(data);
    log.debug("created variable: {}", created);
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
      recipientId,
      type,
      id,
      Optional.ofNullable(variableName).orElse("<Без названия>"),
      Optional.ofNullable(variableValue).orElse("")
    );
    repository.save(data);
    final AutomationVariable<?> created = convert(data);
    log.debug("created variable: {}", created);
    return created;
  }

  private AutomationVariable<?> convert(AutomationVariableData it) {
    if ("string".equals(it.getType())) {
      return new AutomationStringVariable(
        it.getRecipientId(),
        it.getId(),
        it.getName(),
        it.getValue(),
        repository
      );
    }
    if ("number".equals(it.getType())) {
      return new AutomationNumberVariable(
        it.getRecipientId(),
        it.getId(),
        it.getName(),
        Optional.ofNullable(it.getValue())
          .filter(StringUtils::isNotEmpty)
          .map(BigDecimal::new)
          .orElse(BigDecimal.ZERO),
        repository
      );
    }
    return new AutomationVariable<String>(
      it.getRecipientId(),
      it.getId(),
      it.getName(),
      repository
    );
  }
}
