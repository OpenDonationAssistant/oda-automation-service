package io.github.opendonationassistant.wordblacklist;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface WordFilterDataRepository
  extends CrudRepository<WordFilterData, String> {
  public List<WordFilterData> getByRecipientId(String recipientId);

  public void deleteByRecipientId(String recipientId);
}
