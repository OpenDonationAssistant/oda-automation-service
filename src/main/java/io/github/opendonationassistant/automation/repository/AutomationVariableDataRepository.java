package io.github.opendonationassistant.automation.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AutomationVariableDataRepository
  extends CrudRepository<AutomationVariableData, String> {
  public List<AutomationVariableData> getByRecipientId(String recipientId);

  public Optional<AutomationVariableData> getByRecipientIdAndId(
    String recipientId,
    String id
  );
}
