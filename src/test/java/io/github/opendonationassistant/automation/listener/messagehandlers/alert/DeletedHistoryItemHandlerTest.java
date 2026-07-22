package io.github.opendonationassistant.automation.listener.messagehandlers.alert;

import static org.mockito.Mockito.*;

import io.github.opendonationassistant.alert.repository.AlertData;
import io.github.opendonationassistant.alert.repository.AlertDataRepository;
import io.github.opendonationassistant.alert.repository.AlertLink;
import io.github.opendonationassistant.alert.repository.AlertLinkRepository;
import io.github.opendonationassistant.events.history.event.DeletedHistoryItem;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeletedHistoryItemHandlerTest {

  AlertLinkRepository linkRepository = mock(AlertLinkRepository.class);

  AlertDataRepository dataRepository = mock(AlertDataRepository.class);

  DeletedHistoryItemHandler handler = new DeletedHistoryItemHandler(
      mock(ObjectMapper.class),
      linkRepository,
      dataRepository
    );

  ArgumentCaptor<AlertData> alertDataCaptor = ArgumentCaptor.forClass(AlertData.class);

  @Test
  public void testHidesAlertByOriginId() throws IOException {
    var originId = "test-origin-id";
    var alertId = "test-alert-id";
    var event = new DeletedHistoryItem("id", "recipientId", "system", originId);
    var alertLink = new AlertLink(
      "link-id",
      alertId,
      originId,
      "system",
      "event"
    );
    var existingData = new AlertData(
      alertId,
      "recipientId",
      "nickname",
      "message",
      null,
      null,
      null,
      null,
      Instant.now(),
      false
    );

    when(linkRepository.getByOriginId(originId)).thenReturn(List.of(alertLink));
    when(dataRepository.findById(alertId)).thenReturn(
      Optional.of(existingData)
    );

    handler.handle(event);

    verify(dataRepository).save(alertDataCaptor.capture());
    var saved = alertDataCaptor.getValue();
    assert saved.id().equals(alertId);
    assert saved.hidden() == true;
    assert saved.recipientId().equals("recipientId");
  }

  @Test
  public void testDoesNothingWhenOriginIdIsNull() throws IOException {
    var event = new DeletedHistoryItem("id", "recipientId", "system", null);

    handler.handle(event);

    verify(linkRepository, never()).getByOriginId(any());
    verify(dataRepository, never()).findById(any());
  }

  @Test
  public void testHandlesMultipleLinksForSameOriginId() throws IOException {
    var originId = "test-origin-id";
    var alertId1 = "alert-1";
    var alertId2 = "alert-2";

    var event = new DeletedHistoryItem("id", "recipientId", "system", originId);

    var link1 = new AlertLink("link-1", alertId1, originId, "system", "event");
    var link2 = new AlertLink("link-2", alertId2, originId, "system", "event");
    var data1 = new AlertData(
      alertId1,
      "recipientId",
      "nickname",
      "message",
      null,
      null,
      null,
      null,
      Instant.now(),
      false
    );
    var data2 = new AlertData(
      alertId2,
      "recipientId",
      "nickname",
      "message",
      null,
      null,
      null,
      null,
      Instant.now(),
      false
    );

    when(linkRepository.getByOriginId(originId)).thenReturn(
      List.of(link1, link2)
    );
    when(dataRepository.findById(alertId1)).thenReturn(Optional.of(data1));
    when(dataRepository.findById(alertId2)).thenReturn(Optional.of(data2));

    handler.handle(event);

    verify(dataRepository, times(2)).save(alertDataCaptor.capture());
    var allSaved = alertDataCaptor.getAllValues();
    assert allSaved.size() == 2;
    assert allSaved.get(0).hidden() == true;
    assert allSaved.get(1).hidden() == true;
  }
}
