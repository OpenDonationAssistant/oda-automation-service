package io.github.opendonationassistant.automation.domain.reel;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ReelCommand(
  String type,
  String selection,
  String widgetId,
  String paymentId,
  String recipientId
) {}
