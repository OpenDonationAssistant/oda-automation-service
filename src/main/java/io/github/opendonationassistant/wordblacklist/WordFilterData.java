package io.github.opendonationassistant.wordblacklist;

import io.github.opendonationassistant.commons.StringListConverter;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
@MappedEntity("wordblacklist")
public record WordFilterData(
  @Id @MappedProperty(type = DataType.UUID) String id,
  @MappedProperty("recipient_id") String recipientId,
  @MappedProperty(converter = StringListConverter.class) List<String> words
) {}
