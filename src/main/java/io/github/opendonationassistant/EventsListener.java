package io.github.opendonationassistant;

import io.github.opendonationassistant.events.MessageProcessor;
import io.github.opendonationassistant.rabbit.Exchange;
import io.micronaut.messaging.annotation.MessageHeader;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.rabbitmq.bind.RabbitAcknowledgement;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RabbitListener
public class EventsListener {

  private static final String QUEUE_NAME = "automation.events";
  public static final io.github.opendonationassistant.rabbit.Queue QUEUE =
    new io.github.opendonationassistant.rabbit.Queue(QUEUE_NAME);
  public static final List<Exchange> BINDING = List.of(
    Exchange.Exchange(
      "history",
      Map.of(
        "event.MediaHistoryEvent",
        QUEUE,
        "event.ReelResultHistoryEvent",
        QUEUE,
        "event.HistoryItemEvent",
        QUEUE,
        "event.GoalHistoryEvent",
        QUEUE,
        "event.CreateAlertCommand",
        QUEUE
      )
    ),
    Exchange.Exchange("payments", Map.of("event.PaymentEvent", QUEUE))
  );

  private final MessageProcessor processor;

  @Inject
  public EventsListener(MessageProcessor processor) {
    this.processor = processor;
  }

  @Queue(QUEUE_NAME)
  public void checkAutomationForUpdatedGoals(
    @MessageHeader String type,
    byte[] message,
    RabbitAcknowledgement ack
  ) throws IOException {
    processor.process(type, message, ack);
  }
}
