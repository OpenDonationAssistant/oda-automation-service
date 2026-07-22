package io.github.opendonationassistant.alert.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.events.alerts.AlertSender;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.micronaut.core.util.StringUtils;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class AlertRepositoryTest {

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Inject
  AlertDataRepository alertDataRepository;

  @Inject
  AlertLinkRepository alertLinkRepository;

  @Inject
  ObjectMapper objectMapper;

  private UIFacade uiFacade = mock(UIFacade.class);
  private AlertSender alertSender = mock(AlertSender.class);

  @Test
  void testCreateWithSourceAndOriginId(
    @Given AlertData toSave,
    @Given String originId
  ) {
    var alertRepository = new AlertRepository(
      alertLinkRepository,
      alertDataRepository,
      uiFacade,
      alertSender
    );
    Alert result = alertRepository.create("ODA","payment", originId, toSave);
    assertNotNull(result);
    assertNotNull(result.data());
    assertEquals(toSave, result.data());

    var savedData = alertDataRepository.findById(toSave.id());
    assertTrue(savedData.isPresent());
    assertAlertDataEquals(toSave, savedData.get());

    var savedLink = alertLinkRepository.getByOriginId(originId);
    assertEquals(1, savedLink.size());
    assertTrue(StringUtils.isNotEmpty(savedLink.get(0).id()));
    assertEquals(toSave.id(), savedLink.get(0).alertId());
    assertEquals(originId, savedLink.get(0).originId());
    assertEquals("ODA", savedLink.get(0).source());
    assertEquals("payment", savedLink.get(0).event());
  }

  @Test
  void testCreateWithoutSourceAndOriginId(@Given AlertData alertData) {
    var alertRepository = new AlertRepository(
      alertLinkRepository,
      alertDataRepository,
      uiFacade,
      alertSender
    );
    Alert result = alertRepository.create(alertData);

    assertNotNull(result);
    assertEquals(alertData, result.data());
    assertNull(result.link());

    var savedData = alertDataRepository.findById(alertData.id());
    assertTrue(savedData.isPresent());
    assertAlertDataEquals(alertData, savedData.get());

    var missingLink = alertLinkRepository.findById(alertData.id());
    assertTrue(missingLink.isEmpty());
  }

  private void assertAlertDataEquals(AlertData expected, AlertData actual) {
    assertEquals(expected.id(), actual.id());
    assertEquals(expected.recipientId(), actual.recipientId());
    assertEquals(expected.nickname(), actual.nickname());
    assertEquals(expected.message(), actual.message());
    assertEquals(expected.amount(), actual.amount());
    assertEquals(expected.media(), actual.media());
    assertEquals(expected.levelName(), actual.levelName());
    assertEquals(expected.count(), actual.count());
    assertEquals(expected.hidden(), actual.hidden());
    if (expected.createdAt() != null && actual.createdAt() != null) {
      assertEquals(
        roundToMicros(expected.createdAt()),
        roundToMicros(actual.createdAt())
      );
    } else {
      assertEquals(expected.createdAt(), actual.createdAt());
    }
  }

  private static Instant roundToMicros(Instant instant) {
    long nanos = instant.getNano();
    long roundedNanos = (nanos + 500) / 1000 * 1000;
    if (roundedNanos >= 1_000_000_000L) {
      return Instant.ofEpochSecond(instant.getEpochSecond() + 1, roundedNanos - 1_000_000_000L);
    }
    return Instant.ofEpochSecond(instant.getEpochSecond(), roundedNanos);
  }
}
