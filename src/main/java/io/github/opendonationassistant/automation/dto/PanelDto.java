package io.github.opendonationassistant.automation.dto;

import io.github.opendonationassistant.automation.repository.PanelData;
import io.github.opendonationassistant.automation.repository.PanelData.PanelCardData;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import org.jspecify.annotations.Nullable;

@Serdeable
public record PanelDto(String id, String name, List<PanelCardDto> cards) {
  @Serdeable
  public static record PanelCardDto(
    String id,
    String ruleId,
    @Nullable String title
  ) {
    public static PanelCardDto from(PanelCardData card) {
      return new PanelCardDto(card.id(), card.ruleId(), card.title());
    }
    public static PanelCardData toData(PanelCardDto card) {
      return new PanelCardData(card.id(), card.ruleId(), card.title());
    }
  }

  public static PanelDto from(PanelData panel) {
    return new PanelDto(
      panel.id(),
      panel.name(),
      panel.cards().stream().map(PanelCardDto::from).toList()
    );
  }

  public static PanelData toData(String recipientId, PanelDto panel) {
    return new PanelData(
      panel.id(),
      panel.name(),
      recipientId,
      panel.cards().stream().map(PanelCardDto::toData).toList()
    );
  }
}
