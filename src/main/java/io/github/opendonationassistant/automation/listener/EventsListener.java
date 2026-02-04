package io.github.opendonationassistant.automation.listener;

import io.github.opendonationassistant.events.MessageProcessor;
import io.micronaut.messaging.annotation.MessageHeader;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.io.IOException;

@RabbitListener
public class EventsListener {

  private final MessageProcessor processor;

  @Inject
  public EventsListener(MessageProcessor processor) {
    this.processor = processor;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Automation.EVENTS)
  public void checkAutomationForUpdatedGoals(
    @MessageHeader String type,
    byte[] message
  ) throws IOException {
    // TODO Error and ack handling
    processor.process(type, message);
  }
}
