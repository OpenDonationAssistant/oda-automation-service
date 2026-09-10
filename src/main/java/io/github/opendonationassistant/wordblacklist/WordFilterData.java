package io.github.opendonationassistant.wordblacklist;

import io.github.opendonationassistant.commons.StringListConverter;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
@MappedEntity("wordblacklist")
public record WordFilterData(
  @Id String id,
  @MappedProperty("recipient_id") String recipientId,
  @MappedProperty(converter = StringListConverter.class) List<String> words
) {}
