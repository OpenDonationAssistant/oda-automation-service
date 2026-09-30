package io.github.opendonationassistant.meldstudio.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class MeldSettingsRepositoryTest {

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Inject
  public MeldSettingsRepository repository;

  @Inject
  public MeldSettingsDataRepository dataRepository;

  @Test
  public void testUpsertCreatesThenReads(
    @Given String recipientId,
    @Given String websocketUrl
  ) {
    // Act: the first upsert creates a row for the recipient.
    MeldSettingsData created = repository.upsert(recipientId, websocketUrl);

    // Assert: the persisted row has a generated id and the submitted values.
    assertNotNull(created.id());
    assertEquals(recipientId, created.recipientId());
    assertEquals(websocketUrl, created.websocketUrl());

    // Act: read the row back through the owner lookup.
    Optional<MeldSettingsData> found = repository.getByRecipientId(
      recipientId
    );

    // Assert: the stored row matches what was created.
    assertTrue(found.isPresent());
    assertEquals(created.id(), found.get().id());
    assertEquals(recipientId, found.get().recipientId());
    assertEquals(websocketUrl, found.get().websocketUrl());
  }

  @Test
  public void testUpsertUpdatesExistingRowWithoutDuplicate(
    @Given String recipientId,
    @Given String firstUrl,
    @Given String updatedUrl
  ) {
    // Arrange: a row already exists for this recipient.
    MeldSettingsData created = repository.upsert(recipientId, firstUrl);

    // Act: a second upsert targets the same recipient.
    MeldSettingsData updated = repository.upsert(recipientId, updatedUrl);

    // Assert: the existing row is updated in place and keeps its id.
    assertEquals(created.id(), updated.id());
    assertEquals(recipientId, updated.recipientId());
    assertEquals(updatedUrl, updated.websocketUrl());

    // Assert: exactly one row remains for the recipient (no duplicate).
    assertEquals(1L, countRowsFor(recipientId));
    Optional<MeldSettingsData> found = repository.getByRecipientId(
      recipientId
    );
    assertTrue(found.isPresent());
    assertEquals(created.id(), found.get().id());
    assertEquals(updatedUrl, found.get().websocketUrl());
  }

  @Test
  public void testUpsertIsIsolatedPerRecipient(
    @Given String firstRecipientId,
    @Given String secondRecipientId,
    @Given String firstUrl,
    @Given String secondUrl
  ) {
    // Arrange: force distinct owners regardless of generated collisions.
    var firstOwner = firstRecipientId + "-first";
    var secondOwner = secondRecipientId + "-second";

    // Act: two independent owners each store their own settings.
    MeldSettingsData first = repository.upsert(firstOwner, firstUrl);
    MeldSettingsData second = repository.upsert(secondOwner, secondUrl);

    // Assert: the second owner receives a distinct row and id.
    assertNotEquals(first.id(), second.id());
    assertEquals(secondOwner, second.recipientId());
    assertEquals(secondUrl, second.websocketUrl());

    // Assert: the first owner's row is untouched by the second upsert.
    Optional<MeldSettingsData> storedFirst = repository.getByRecipientId(
      firstOwner
    );
    assertTrue(storedFirst.isPresent());
    assertEquals(first.id(), storedFirst.get().id());
    assertEquals(firstUrl, storedFirst.get().websocketUrl());

    Optional<MeldSettingsData> storedSecond = repository.getByRecipientId(
      secondOwner
    );
    assertTrue(storedSecond.isPresent());
    assertEquals(second.id(), storedSecond.get().id());
    assertEquals(secondUrl, storedSecond.get().websocketUrl());
  }

  private long countRowsFor(String recipientId) {
    long count = 0;
    for (MeldSettingsData row : dataRepository.findAll()) {
      if (recipientId.equals(row.recipientId())) {
        count++;
      }
    }
    return count;
  }
}
