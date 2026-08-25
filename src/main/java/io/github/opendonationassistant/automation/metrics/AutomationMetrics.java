package io.github.opendonationassistant.automation.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@Singleton
public class AutomationMetrics {

  public static final String ITERATION_RUNS_METRIC_NAME =
    "automation.iteration.runs";
  public static final String EVENTS_HANDLED_METRIC_NAME =
    "automation.events.handled";
  public static final String SOURCE_TAG = "source";
  public static final String TYPE_TAG = "type";
  public static final String UNKNOWN = "unknown";

  private final MeterRegistry registry;

  @Inject
  public AutomationMetrics(MeterRegistry registry) {
    this.registry = registry;
  }

  public void iterationRun(@Nullable Object source) {
    registry
      .counter(
        ITERATION_RUNS_METRIC_NAME,
        SOURCE_TAG,
        Optional
          .ofNullable(source)
          .map(it -> it.getClass().getSimpleName())
          .orElse(UNKNOWN)
      )
      .increment();
  }

  public void eventHandled(@Nullable String type) {
    registry
      .counter(
        EVENTS_HANDLED_METRIC_NAME,
        TYPE_TAG,
        Optional.ofNullable(type).orElse(UNKNOWN)
      )
      .increment();
  }
}