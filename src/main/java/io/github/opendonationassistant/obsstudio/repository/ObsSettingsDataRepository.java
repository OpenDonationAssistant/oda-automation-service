package io.github.opendonationassistant.obsstudio.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ObsSettingsDataRepository
  extends CrudRepository<ObsSettingsData, String> {
  public Optional<ObsSettingsData> getByRecipientId(String recipientId);
}
