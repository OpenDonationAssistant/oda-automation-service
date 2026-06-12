package io.github.opendonationassistant;

import io.github.opendonationassistant.automation.domain.IterationFactory;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.HasRecipientId;
import io.github.opendonationassistant.events.twitch.events.TwitchChannelRaidEvent;
import io.github.opendonationassistant.events.twitch.events.TwitchStreamStartedEvent;
import io.github.opendonationassistant.rabbit.Exchange;
import io.micronaut.messaging.annotation.MessageHeader;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.rabbitmq.bind.RabbitAcknowledgement;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RabbitListener
public class ProcessingListener {

  public static final String QUEUE_NAME = "automation.processing";
  public static final io.github.opendonationassistant.rabbit.Queue QUEUE =
    new io.github.opendonationassistant.rabbit.Queue(QUEUE_NAME);
  public static final List<Exchange> BINDING = List.of(
    Exchange.Exchange("twitch", Map.of("event.*", QUEUE))
  );

  private ODALogger log = new ODALogger(this);
  private final IterationFactory iterationFactory;
  private final ObjectMapper mapper;

  @Inject
  public ProcessingListener(
    IterationFactory iterationFactory,
    ObjectMapper mapper
  ) {
    this.mapper = mapper;
    this.iterationFactory = iterationFactory;
  }

  @Queue(QUEUE_NAME)
  public void processStep(
    @MessageHeader String type,
    byte[] message,
    RabbitAcknowledgement ack
  ) throws IOException {
    try {
      convert(type, message).ifPresent(event -> {
        if (event instanceof HasRecipientId source) {
          log.debug("Processing step", Map.of("type", type, "source", source));
          iterationFactory.create(source.recipientId(), source).run();
        }
        log.debug(
          "Step missing recipientId",
          Map.of("type", type, "event", event)
        );
      });
      ack.ack();
      log.debug("Step processed", Map.of("type", type));
    } catch (Exception e) {
      log.error(
        "Error processing step",
        Map.of("error", e.getLocalizedMessage())
      );
    }
  }

  private Optional<Object> convert(String type, byte[] message)
    throws IOException {
    switch (type) {
      case "event.TwitchStreamStartedEvent":
        return Optional.ofNullable(
          mapper.readValue(message, TwitchStreamStartedEvent.class)
        );
      case "event.TwitchChannelRaidEvent":
        return Optional.ofNullable(
          mapper.readValue(message, TwitchChannelRaidEvent.class)
        );
    }
    log.debug("Can't convert step", Map.of("type", type));
    return Optional.empty();
  }
}
