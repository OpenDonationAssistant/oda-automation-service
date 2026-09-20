package io.github.opendonationassistant.automation.repository;

import io.github.opendonationassistant.automation.repository.PanelData.PanelCardData;
import java.util.List;

public class Panel {

  private PanelData data;
  private final PanelDataRepository dataRepository;

  public Panel(PanelData data, PanelDataRepository dataRepository) {
    this.data = data;
    this.dataRepository = dataRepository;
  }

  public Panel updateNameAndCards(String name, List<PanelCardData> cards) {
    this.data = this.data.withName(name).withCards(cards);
    dataRepository.update(this.data);
    return this;
  }

  public PanelData data() {
    return data;
  }

  public void save() {
    dataRepository.update(data);
  }

  public void delete() {
    dataRepository.deleteById(data.id());
  }
}
