package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.alert.repository.AlertData;
import io.github.opendonationassistant.alert.repository.AlertRepository;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.time.Instant;

@Singleton
public class PaymentEventHandler extends AbstractMessageHandler<PaymentEvent> {

  private final AlertRepository repository;
  private final WordFilterRepository wordFilterRepository;

  public PaymentEventHandler(
    ObjectMapper mapper,
    AlertRepository repository,
    WordFilterRepository wordFilterRepository
  ) {
    super(mapper);
    this.repository = repository;
    this.wordFilterRepository = wordFilterRepository;
  }

  @Override
  public void handle(PaymentEvent message) throws IOException {
    var wordFilter = wordFilterRepository.getByRecipientId(
      message.recipientId()
    );
    var data = new AlertData(
      Generators.timeBasedEpochGenerator().generate().toString(),
      message.recipientId(),
      wordFilter.filter(message.cleanNickname()),
      wordFilter.filter(message.cleanMessage()),
      message.amount(),
      null,
      null,
      null,
      Instant.now(),
      false
    );
    repository.create("ODA", "payment", message.id(), data);
  }
}
