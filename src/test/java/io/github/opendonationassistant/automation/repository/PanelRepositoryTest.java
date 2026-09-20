package io.github.opendonationassistant.automation.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class PanelRepositoryTest {

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Inject
  public PanelRepository repository;

  @Test
  public void testCreateReadUpdateDeleteRoundTrip(
    @Given String recipientId,
    @Given String name
  ) {
    // Arrange: one titled card and one null-title card to exercise @Nullable.
    var titledCard = new PanelData.PanelCardData(
      "card-1",
      "rule-1",
      "First card"
    );
    var untitledCard = new PanelData.PanelCardData("card-2", "rule-2", null);

    // Act + Assert: create persists every field.
    var created = repository.create(
      recipientId,
      name,
      List.of(titledCard, untitledCard)
    );
    assertEquals(recipientId, created.data().recipientId());
    assertNotNull(created.data().id());
    assertEquals(name, created.data().name());
    assertEquals(List.of(titledCard, untitledCard), created.data().cards());

    // Act + Assert: getByRecipientIdAndId reads the created row back.
    Optional<Panel> createdPanel = repository.getByRecipientIdAndId(
      recipientId,
      created.data().id()
    );
    assertTrue(createdPanel.isPresent());
    PanelData foundPanel = createdPanel.get().data();
    assertEquals(recipientId, foundPanel.recipientId());
    assertEquals(created.data().id(), foundPanel.id());
    assertEquals(name, foundPanel.name());
    assertEquals(List.of(titledCard, untitledCard), foundPanel.cards());

    // Act + Assert: listByRecipientId includes the created panel.
    List<Panel> listed = repository.listByRecipientId(recipientId).toList();
    assertTrue(
      listed.stream().anyMatch(it -> it.data().id().equals(created.data().id()))
    );

    // Act + Assert: update replaces name and cards.
    var updatedCard = new PanelData.PanelCardData(
      "card-3",
      "rule-3",
      "Updated card"
    );
    var updatedName = name + "-updated";
    createdPanel.get().updateNameAndCards(updatedName, List.of(updatedCard));
    Optional<Panel> updatedPanel = repository.getByRecipientIdAndId(
      recipientId,
      created.data().id()
    );
    assertTrue(updatedPanel.isPresent());
    assertEquals(updatedName, updatedPanel.get().data().name());
    assertEquals(List.of(updatedCard), updatedPanel.get().data().cards());

    // Act + Assert: delete removes the row for this owner only.
    updatedPanel.get().delete();
    assertTrue(
      repository.getByRecipientIdAndId(recipientId, created.data().id()).isEmpty()
    );
    assertTrue(
      repository
        .listByRecipientId(recipientId)
        .noneMatch(it -> it.data().id().equals(created.data().id()))
    );
  }

  @Test
  public void testGetAndListUnknownPanelReturnEmpty(
    @Given String recipientId,
    @Given String id
  ) {
    // Act + Assert: an unknown panel is neither fetched nor listed.
    assertTrue(repository.getByRecipientIdAndId(recipientId, id).isEmpty());
    assertTrue(repository.listByRecipientId(recipientId).findAny().isEmpty());
  }
}
