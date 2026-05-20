package io.github.opendonationassistant.automation.domain.twitch;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record SendAndPinChatMessageCommand(
  String recipientId,
  String refreshTokenId,
  String message
) {}
