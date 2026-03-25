package io.github.opendonationassistant.alert.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.events.alerts.AlertSender;
import io.github.opendonationassistant.events.ui.UIFacade;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
public class AlertRepository {

  private final AlertLinkRepository linkRepository;
  private final AlertDataRepository dataRepository;
  private final UIFacade uiFacade;
  private final AlertSender alertSender;

  @Inject
  public AlertRepository(
    AlertLinkRepository linkRepository,
    AlertDataRepository dataRepository,
    UIFacade uiFacade,
    AlertSender alertSender
  ) {
    this.linkRepository = linkRepository;
    this.dataRepository = dataRepository;
    this.uiFacade = uiFacade;
    this.alertSender = alertSender;
  }

  public Alert create(AlertData alert) {
    dataRepository.save(alert);
    return new Alert(alert, null, alertSender, uiFacade);
  }

  public Alert create(String source, String originId, AlertData alert) {
    dataRepository.save(alert);
    final AlertLink alertLink = new AlertLink(
      Generators.timeBasedEpochGenerator().generate().toString(),
      alert.id(),
      originId,
      source
    );
    linkRepository.save(alertLink);
    return new Alert(alert, alertLink, alertSender, uiFacade);
  }

  public Optional<Alert> get(String id) {
    return dataRepository.findById(id).map(this::convert);
  }

  private Alert convert(AlertData data) {
    return new Alert(
      data,
      Optional.ofNullable(
        linkRepository.getByAlertId(data.id()).getFirst()
      ).orElse(null),
      alertSender,
      uiFacade
    );
  }
}
