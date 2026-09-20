package io.github.opendonationassistant.automation.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PanelDataRepository
  extends PageableRepository<PanelData, String> {
  public List<PanelData> getByRecipientId(String recipientId);

  public Optional<PanelData> getByRecipientIdAndId(
    String recipientId,
    String id
  );
}
