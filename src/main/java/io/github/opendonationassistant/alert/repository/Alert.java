package io.github.opendonationassistant.alert.repository;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.events.alerts.AlertNotification;
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
        null, // alertmedia
        Optional.ofNullable(link).map(AlertLink::source).orElse("manual")
      )
    );
    var variables = new ArrayList<Variable>();
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
    Optional.ofNullable(data.message()).ifPresent(message ->
      variables.add(
        new Variable(uuid.generate().toString(), "message", message, "string")
      )
    );
    var event = new Event(data.id(), "Alert", variables);
    ui.sendEvent(data.recipientId(), event);
  }
}
