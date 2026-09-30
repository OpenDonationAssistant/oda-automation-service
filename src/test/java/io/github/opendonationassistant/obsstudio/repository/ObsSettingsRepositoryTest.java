package io.github.opendonationassistant.obsstudio.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class ObsSettingsRepositoryTest {

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Inject
  public ObsSettingsRepository repository;

  @Inject
  public ObsSettingsDataRepository dataRepository;

  @Test
  public void testUpsertCreatesAndReadsBackSettings(
    @Given String recipientId,
    @Given String websocketUrl,
    @Given String password
  ) {
    var created = repository.upsert(recipientId, websocketUrl, password);

    assertNotNull(created.id());
    assertEquals(recipientId, created.recipientId());
    assertEquals(websocketUrl, created.websocketUrl());
    assertEquals(password, created.password());

    Optional<ObsSettingsData> found = repository.getByRecipientId(recipientId);

    assertTrue(found.isPresent());
    assertEquals(created, found.get());
  }

  @Test
  public void testUpsertWithNullPasswordPersistsNull(
    @Given String recipientId,
    @Given String websocketUrl
  ) {
    var created = repository.upsert(recipientId, websocketUrl, null);

    assertNotNull(created.id());
    assertNull(created.password());

    Optional<ObsSettingsData> found = repository.getByRecipientId(recipientId);
    assertTrue(found.isPresent());
    assertNull(found.get().password());
    assertEquals(websocketUrl, found.get().websocketUrl());
  }

  @Test
  public void testUpsertTwiceUpdatesExistingRowWithoutDuplicating(
    @Given String recipientId,
    @Given String firstWebsocketUrl,
    @Given String firstPassword,
    @Given String secondWebsocketUrl,
    @Given String secondPassword
  ) {
    var created = repository.upsert(
      recipientId,
      firstWebsocketUrl,
      firstPassword
    );

    var updated = repository.upsert(
      recipientId,
      secondWebsocketUrl,
      secondPassword
    );

    assertEquals(created.id(), updated.id());
    assertEquals(secondWebsocketUrl, updated.websocketUrl());
    assertEquals(secondPassword, updated.password());

    Optional<ObsSettingsData> found = repository.getByRecipientId(recipientId);
    assertTrue(found.isPresent());
    assertEquals(created.id(), found.get().id());
    assertEquals(secondWebsocketUrl, found.get().websocketUrl());

    long rows = StreamSupport.stream(
      dataRepository.findAll().spliterator(),
      false
    )
      .filter(row -> recipientId.equals(row.recipientId()))
      .count();
    assertEquals(1, rows);
  }

  @Test
  public void testUpsertForDifferentRecipientsAreIsolated(
    @Given String firstRecipientId,
    @Given String firstWebsocketUrl,
    @Given String secondRecipientId,
    @Given String secondWebsocketUrl
  ) {
    var firstOwner = firstRecipientId + "-first";
    var secondOwner = secondRecipientId + "-second";

    var first = repository.upsert(firstOwner, firstWebsocketUrl, null);

    var second = repository.upsert(secondOwner, secondWebsocketUrl, null);

    assertNotEquals(first.id(), second.id());
    assertEquals(
      firstWebsocketUrl,
      repository.getByRecipientId(firstOwner).orElseThrow().websocketUrl()
    );
    assertEquals(
      secondWebsocketUrl,
      repository.getByRecipientId(secondOwner).orElseThrow().websocketUrl()
    );
  }
}
