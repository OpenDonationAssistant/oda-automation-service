package io.github.opendonationassistant.alert.repository;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.events.alerts.AlertNotification;
import io.github.opendonationassistant.events.alerts.AlertNotification.AlertMedia;
import io.github.opendonationassistant.events.alerts.AlertSender;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import java.util.ArrayList;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

public class Alert {

  private final AlertData data;

  @Nullable
  private final AlertLink link;

  private final AlertSender sender;
  private final UIFacade ui;
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();

  public Alert(
    AlertData data,
    @Nullable AlertLink link,
    AlertSender sender,
    UIFacade facade
  ) {
    this.data = data;
    this.link = link;
    this.sender = sender;
    this.ui = facade;
  }

  public AlertData data() {
    return data;
  }

  public @Nullable AlertLink link() {
    return link;
  }

  public void send() {
    sender.send(
      data.recipientId(),
      new AlertNotification(
        data.id(),
        data.nickname(),
        data.message(),
        data.recipientId(),
        data.amount(),
        Optional.ofNullable(data.media())
          .map(it -> it.url())
          .map(AlertMedia::new)
          .orElse(null), // new AlertMedia(data.media().url()), // alertmedia
        Optional.ofNullable(link).map(AlertLink::source).orElse("manual")
      )
    );

    var variables = new ArrayList<Variable>();
    Optional.ofNullable(link)
      .map(link -> link.source())
      .ifPresent(system ->
        variables.add(
          new Variable(uuid.generate().toString(), "system", system, "string")
        )
      );
    Optional.ofNullable(link)
      .map(link -> link.originId())
      .ifPresent(originId ->
        variables.add(
          new Variable(
            uuid.generate().toString(),
            "originId",
            originId,
            "string"
          )
        )
      );
    Optional.ofNullable(link)
      .map(link -> link.event())
      .ifPresent(event ->
        variables.add(
          new Variable(uuid.generate().toString(), "event", event, "string")
        )
      );
    Optional.ofNullable(data.nickname()).ifPresent(nickname ->
      variables.add(
        new Variable(uuid.generate().toString(), "nickname", nickname, "string")
      )
    );
    Optional.ofNullable(data.amount())
      .map(amount -> amount.getMajor())
      .map(String::valueOf)
      .ifPresent(major ->
        variables.add(
          new Variable(uuid.generate().toString(), "amount", major, "string")
        )
      );
    Optional.ofNullable(data.media())
      .map(media -> media.url())
      .map(String::valueOf)
      .ifPresent(url ->
        variables.add(
          new Variable(uuid.generate().toString(), "alertmedia", url, "string")
        )
      );
    Optional.ofNullable(data.message()).ifPresent(message ->
      variables.add(
        new Variable(uuid.generate().toString(), "message", message, "string")
      )
    );
    Optional.ofNullable(data.levelName()).ifPresent(levelname ->
      variables.add(
        new Variable(
          uuid.generate().toString(),
          "levelName",
          levelname,
          "string"
        )
      )
    );
    Optional.ofNullable(data.count()).ifPresent(count ->
      variables.add(
        new Variable(uuid.generate().toString(), "count", count, "number")
      )
    );
    var event = new Event(data.id(), "Alert", variables);
    ui.sendEvent(data.recipientId(), event);
  }
}
