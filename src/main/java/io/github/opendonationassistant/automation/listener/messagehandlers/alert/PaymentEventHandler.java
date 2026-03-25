package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.alert.repository.AlertData;
import io.github.opendonationassistant.alert.repository.AlertRepository;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;

public class PaymentEventHandler extends AbstractMessageHandler<PaymentEvent> {

  private final AlertRepository repository;

  public PaymentEventHandler(ObjectMapper mapper, AlertRepository repository) {
    super(mapper);
    this.repository = repository;
  }

  @Override
  public void handle(PaymentEvent message) throws IOException {
    var data = new AlertData(
      Generators.timeBasedEpochGenerator().generate().toString(),
      message.recipientId(),
      message.nickname(),
      message.message(),
      message.amount()
    );
    repository.create("payment", message.id(), data);
  }
}
