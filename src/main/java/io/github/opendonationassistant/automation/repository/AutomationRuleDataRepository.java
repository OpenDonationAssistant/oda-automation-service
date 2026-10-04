package io.github.opendonationassistant.automation.repository;

import io.micronaut.data.annotation.Query;
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

  @Query(
    value = "SELECT * FROM automation.automationrule " +
    "WHERE recipient_id = :recipientId " +
    "AND triggers @> jsonb_build_array(jsonb_build_object('id', CAST(:trigger AS text)))",
    countQuery = "SELECT COUNT(*) FROM automation.automationrule " +
    "WHERE recipient_id = :recipientId " +
    "AND triggers @> jsonb_build_array(jsonb_build_object('id', CAST(:trigger AS text)))"
  )
  public Page<AutomationRuleData> findByRecipientIdAndTrigger(
    String recipientId,
    String trigger,
    Pageable pageable
  );

  public Optional<AutomationRuleData> getByRecipientIdAndId(
    String recipientId,
    String id
  );
}
