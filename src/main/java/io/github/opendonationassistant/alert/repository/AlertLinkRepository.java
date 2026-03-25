package io.github.opendonationassistant.alert.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AlertLinkRepository extends CrudRepository<AlertLink, String> {
  public List<AlertLink> getByOriginId(String originId);
  public List<AlertLink> getByAlertId(String alertId);
}
