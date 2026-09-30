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
    MeldSettingsData created = repository.upsert(recipientId, websocketUrl);

    assertNotNull(created.id());
    assertEquals(recipientId, created.recipientId());
    assertEquals(websocketUrl, created.websocketUrl());

    Optional<MeldSettingsData> found = repository.getByRecipientId(
      recipientId
    );

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
    MeldSettingsData created = repository.upsert(recipientId, firstUrl);

    MeldSettingsData updated = repository.upsert(recipientId, updatedUrl);

    assertEquals(created.id(), updated.id());
    assertEquals(recipientId, updated.recipientId());
    assertEquals(updatedUrl, updated.websocketUrl());

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
    var firstOwner = firstRecipientId + "-first";
    var secondOwner = secondRecipientId + "-second";

    MeldSettingsData first = repository.upsert(firstOwner, firstUrl);
    MeldSettingsData second = repository.upsert(secondOwner, secondUrl);

    assertNotEquals(first.id(), second.id());
    assertEquals(secondOwner, second.recipientId());
    assertEquals(secondUrl, second.websocketUrl());

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
