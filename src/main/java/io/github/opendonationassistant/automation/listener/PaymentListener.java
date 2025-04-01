package io.github.opendonationassistant.automation.listener;

import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;

// @RabbitListener
public class PaymentListener {

  private AutomationRuleRepository ruleRepository;

  // @Queue(io.github.opendonationassistant.rabbit.Queue.Payments.AUTOMATION)
  // public void listen(CompletedPaymentNotification payment) {
  //   var rules = ruleRepository.listByRecipientId(payment.getRecipientId());
  //   rules.stream();
  // }
}
