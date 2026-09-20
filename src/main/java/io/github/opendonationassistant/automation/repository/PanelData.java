package io.github.opendonationassistant.automation.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.Wither;
import java.util.List;
import org.jspecify.annotations.Nullable;

@MappedEntity("automationpanel")
@Serdeable
@Wither
public record PanelData(
  @Id String id,
  String name,
  String recipientId,
  @MappedProperty(type = DataType.JSON) List<PanelCardData> cards
)
  implements PanelDataWither {
  @Serdeable
  public static record PanelCardData(
    String id,
    String ruleId,
    @Nullable String title
  ) {}
}
