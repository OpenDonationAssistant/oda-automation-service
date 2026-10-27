package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.automation.AutomationVariable;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.domain.IterationFactory;
import io.github.opendonationassistant.automation.listener.messagehandlers.twitch.TwitchChannelRaidEventHandler;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.history.event.HistoryItemEvent;
import io.micronaut.rabbitmq.bind.RabbitAcknowledgement;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

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

  @Test
  public void testConvertsAndDispatchesHistoryItemEvent() throws IOException {
    // Objective: a HistoryItemEvent payload is deserialized into the domain
    // event, dispatched to the iteration engine with its recipientId, and acked.
    var event = new HistoryItemEvent(
      "id",
      "payment",
      "recipient-1",
      "ODA",
      "origin",
      Instant.now(),
      "nickname",
      new Amount(100, 0, "RUB"),
      "hello",
      List.of(),
      List.of(),
      null
    );
    doReturn(iteration).when(iterationFactory).create(any(), any());

    new ProcessingListener(iterationFactory, mapper).processStep(
      "HistoryItemEvent",
      mapper.writeValueAsBytes(event),
      ack
    );

    // Micronaut serde deserializes empty JSON arrays as null, so compare the
    // meaningful fields of the dispatched payload rather than the whole record.
    ArgumentCaptor<HistoryItemEvent> captor = ArgumentCaptor.forClass(
      HistoryItemEvent.class
    );
    verify(iterationFactory).create(eq(event.recipientId()), captor.capture());
    var dispatched = captor.getValue();
    assertEquals("id", dispatched.id());
    assertEquals("recipient-1", dispatched.recipientId());
    assertEquals("nickname", dispatched.nickname());
    assertEquals("hello", dispatched.message());
    verify(iteration).run();
    verify(ack).ack();
  }

  @Test
  public void testConvertsAndDispatchesVariableCreatedEvent() throws IOException {
    // Objective: a VariableCreated payload is deserialized, dispatched to the
    // iteration engine with its recipientId, and acked.
    var event = new AutomationVariable.VariableCreated(
      "variable-1",
      "recipient-1",
      "greeting",
      "string",
      "hello"
    );
    doReturn(iteration).when(iterationFactory).create(any(), any());

    new ProcessingListener(iterationFactory, mapper).processStep(
      "VariableCreated",
      mapper.writeValueAsBytes(event),
      ack
    );

    verify(iterationFactory).create(event.recipientId(), event);
    verify(iteration).run();
    verify(ack).ack();
  }

  @Test
  public void testConvertsAndDispatchesVariableUpdatedEvent() throws IOException {
    // Objective: a VariableUpdated payload is deserialized, dispatched to the
    // iteration engine with its recipientId, and acked.
    var event = new AutomationVariable.VariableUpdated(
      "variable-1",
      "recipient-1",
      "greeting",
      "string",
      "hello"
    );
    doReturn(iteration).when(iterationFactory).create(any(), any());

    new ProcessingListener(iterationFactory, mapper).processStep(
      "VariableUpdated",
      mapper.writeValueAsBytes(event),
      ack
    );

    verify(iterationFactory).create(event.recipientId(), event);
    verify(iteration).run();
    verify(ack).ack();
  }

  @Test
  public void testConvertsAndDispatchesVariableDeletedEvent() throws IOException {
    // Objective: a VariableDeleted payload is deserialized, dispatched to the
    // iteration engine with its recipientId, and acked.
    var event = new AutomationVariable.VariableDeleted(
      "variable-1",
      "recipient-1",
      "greeting",
      "string",
      "hello"
    );
    doReturn(iteration).when(iterationFactory).create(any(), any());

    new ProcessingListener(iterationFactory, mapper).processStep(
      "VariableDeleted",
      mapper.writeValueAsBytes(event),
      ack
    );

    verify(iterationFactory).create(event.recipientId(), event);
    verify(iteration).run();
    verify(ack).ack();
  }
}
