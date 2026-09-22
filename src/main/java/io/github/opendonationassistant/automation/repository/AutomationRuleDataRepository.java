package io.github.opendonationassistant.automation.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AutomationRuleDataRepository
  extends PageableRepository<AutomationRuleData, String> {
  public List<AutomationRuleData> getByRecipientId(String recipientId);

  public Page<AutomationRuleData> findByRecipientId(
    String recipientId,
    Pageable pageable
  );

  public Optional<AutomationRuleData> getByRecipientIdAndId(
    String recipientId,
    String id
  );
}
