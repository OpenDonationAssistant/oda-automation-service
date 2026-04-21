package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.alert.repository.AlertData;
import io.github.opendonationassistant.alert.repository.AlertRepository;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@Singleton
public class CreateAlertCommandHandler
  extends AbstractMessageHandler<
    io.github.opendonationassistant.automation.listener.messagehandlers.alert.CreateAlertCommandHandler.CreateAlertCommand
  > {

  private final AlertRepository repository;

  public CreateAlertCommandHandler(
    ObjectMapper mapper,
    AlertRepository repository
  ) {
    super(mapper);
    this.repository = repository;
  }

  @Override
  public void handle(CreateAlertCommand message) throws IOException {
    var data = new AlertData(
      Generators.timeBasedEpochGenerator().generate().toString(),
      message.recipientId(),
      message.nickname(),
      message.message(),
      message.amount(),
      Optional.ofNullable(message.url())
        .map(url -> new AlertData.AlertMedia(url))
        .orElse(null),
      message.levelName(),
      message.count()
    );
    repository.create(
      message.system(),
      message.event(),
      message.paymentId(),
      data
    );
  }

  @Serdeable
  public static record CreateAlertCommand(
    String paymentId,
    String recipientId,
    String nickname,
    String message,
    Amount amount,
    @Nullable String url,
    String system,
    String event,
    @Nullable Integer count,
    @Nullable String levelName
  ) {}
}
