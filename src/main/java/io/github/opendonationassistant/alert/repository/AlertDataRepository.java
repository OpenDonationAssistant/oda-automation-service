package io.github.opendonationassistant.alert.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import io.micronaut.data.repository.jpa.JpaSpecificationExecutor;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AlertDataRepository
  extends
    PageableRepository<AlertData, String>,
    JpaSpecificationExecutor<AlertData> {}
