package io.github.opendonationassistant.automation.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AutomationVariableDataRepository
  extends PageableRepository<AutomationVariableData, String> {

  public List<AutomationVariableData> getByRecipientId(String recipientId);

  public Page<AutomationVariableData> findByRecipientId(
    String recipientId,
    Pageable pageable
  );

  public Optional<AutomationVariableData> getByRecipientIdAndId(
    String recipientId,
    String id
  );
}
