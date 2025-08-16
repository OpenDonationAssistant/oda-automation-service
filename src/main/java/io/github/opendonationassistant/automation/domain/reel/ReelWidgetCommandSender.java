package io.github.opendonationassistant.automation.domain.reel;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;

@RabbitClient("amq.topic")
public interface ReelWidgetCommandSender {
  void send(@Binding String binding, ReelCommand command);
}
