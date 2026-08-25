package io.github.opendonationassistant.automation.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.opendonationassistant.events.twitch.events.TwitchStreamStartedEvent;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

public class AutomationMetricsTest {

  @Test
  public void countsIterationRunsPerSource() {
    var registry = new SimpleMeterRegistry();
    var metrics = new AutomationMetrics(registry);

    metrics.iterationRun(
      new TwitchStreamStartedEvent("eventId", "testuser", "url")
    );
    metrics.iterationRun(
      new TwitchStreamStartedEvent("eventId", "testuser", "url")
    );
    metrics.iterationRun(null);

    assertEquals(
      2,
      registry
        .counter(
          AutomationMetrics.ITERATION_RUNS_METRIC_NAME,
          AutomationMetrics.SOURCE_TAG,
          "TwitchStreamStartedEvent"
        )
        .count()
    );
    assertEquals(
      1,
      registry
        .counter(
          AutomationMetrics.ITERATION_RUNS_METRIC_NAME,
          AutomationMetrics.SOURCE_TAG,
          AutomationMetrics.UNKNOWN
        )
        .count()
    );
  }

  @Test
  public void countsHandledEventsPerType() {
    var registry = new SimpleMeterRegistry();
    var metrics = new AutomationMetrics(registry);

    metrics.eventHandled("MediaHistoryEvent");
    metrics.eventHandled("PaymentEvent");
    metrics.eventHandled("PaymentEvent");
    metrics.eventHandled(null);

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
    assertEquals(
      1,
      registry
        .counter(
          AutomationMetrics.EVENTS_HANDLED_METRIC_NAME,
          AutomationMetrics.TYPE_TAG,
          AutomationMetrics.UNKNOWN
        )
        .count()
    );
  }
}