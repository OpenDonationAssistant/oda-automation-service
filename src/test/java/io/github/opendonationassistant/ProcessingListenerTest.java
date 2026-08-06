package io.github.opendonationassistant;

import static org.mockito.Mockito.*;

import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.IterationFactory;
import io.github.opendonationassistant.automation.listener.messagehandlers.twitch.TwitchChannelRaidEventHandler;
import io.micronaut.rabbitmq.bind.RabbitAcknowledgement;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
public class ProcessingListenerTest {

  ObjectMapper mapper = ObjectMapper.getDefault();
  ObjectMapper mockedMapper = mock(ObjectMapper.class);
  IterationFactory iterationFactory = mock(IterationFactory.class);
  RabbitAcknowledgement ack = mock(RabbitAcknowledgement.class);
  Iteration iteration = mock(Iteration.class);

  @Test
  public void testTriggeringCorrectTrigger(
    @Given TwitchChannelRaidEventHandler.TwitchChannelRaidEvent event
  ) throws IOException {
    doReturn(iteration).when(iterationFactory).create(any(), any());

    new ProcessingListener(iterationFactory, mapper).processStep(
      "TwitchChannelRaidEvent",
      mapper.writeValueAsBytes(event),
      ack
    );

    verify(iterationFactory).create(event.recipientId(), event);
    verify(iteration).run();
    verify(ack).ack();
  }

  @Test
  public void testNotAckingMessageOnException(
    @Given TwitchChannelRaidEventHandler.TwitchChannelRaidEvent event
  ) throws IOException {
    doReturn(iteration).when(iterationFactory).create(any(), any());
    doThrow(new RuntimeException()).when(iteration).run();

    new ProcessingListener(iterationFactory, mapper).processStep(
      "TwitchChannelRaidEvent",
      mapper.writeValueAsBytes(event),
      ack
    );

    verifyNoInteractions(ack);
  }
}
