package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.events.MessageProcessor;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micronaut.rabbitmq.bind.RabbitAcknowledgement;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EventsListenerTest {

  @Test
  public void countsHandledEventsByType() throws IOException {
    var processor = new MessageProcessor(List.of());
    var registry = new SimpleMeterRegistry();
    var ack = mock(RabbitAcknowledgement.class);

    var listener = new EventsListener(processor, new AutomationMetrics(registry));
    listener.checkAutomationForUpdatedGoals(
      "MediaHistoryEvent",
      new byte[0],
      ack
    );
    listener.checkAutomationForUpdatedGoals("PaymentEvent", new byte[0], ack);
    listener.checkAutomationForUpdatedGoals("PaymentEvent", new byte[0], ack);

    assertEquals(
      1,
      registry
        .counter(
          AutomationMetrics.EVENTS_HANDLED_METRIC_NAME,
          AutomationMetrics.TYPE_TAG,
          "MediaHistoryEvent"
        )
        .count()
    );
    assertEquals(
      2,
      registry
        .counter(
          AutomationMetrics.EVENTS_HANDLED_METRIC_NAME,
          AutomationMetrics.TYPE_TAG,
          "PaymentEvent"
        )
        .count()
    );
    verify(ack, times(3)).ack();
  }
}