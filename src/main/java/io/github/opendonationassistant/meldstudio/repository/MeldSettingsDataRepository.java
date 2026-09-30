package io.github.opendonationassistant.meldstudio.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MeldSettingsDataRepository
  extends CrudRepository<MeldSettingsData, String> {
  public Optional<MeldSettingsData> getByRecipientId(String recipientId);
}
