package io.github.opendonationassistant.alert.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.alert.repository.AlertData;
import io.github.opendonationassistant.alert.repository.AlertDataRepository;
import io.github.opendonationassistant.testutils.AuthenticationGenerator;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration tests for {@link AlertController#listAlerts}.
 *
 * <p>Boots the Micronaut context with the {@code allinone} environment so the
 * PostgreSQL database is provisioned by Micronaut Test Resources, and exercises
 * the controller end-to-end through the real {@link AlertDataRepository} (JPA
 * specification query + pagination) rather than a mock.
 *
 * <p>Each test seeds rows under a unique owner id so the shared database does
 * not leak state between tests.
 */
@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class AlertControllerTest {

  private static final Instant BASE = Instant.parse("2026-01-01T00:00:00Z");

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Inject
  AlertController controller;

  @Inject
  AlertDataRepository repository;

  @Test
  public void testListAlertsReturnsOnlyOwnerAlerts(
    @Given String owner,
    @Given String other
  ) {
    // Arrange: two alerts for the owner and one for somebody else.
    final String ownerId = owner + "-owner";
    final String otherId = other + "-other";
    repository.save(alert(ownerId + "-1", ownerId, BASE));
    repository.save(alert(ownerId + "-2", ownerId, BASE.plusSeconds(1)));
    repository.save(alert(otherId, otherId, BASE));

    // Act
    final HttpResponse<Page<AlertData>> response = controller.listAlerts(
      AuthenticationGenerator.forUser(ownerId),
      null,
      null,
      Pageable.unpaged()
    );

    // Assert
    assertEquals(HttpStatus.OK, response.getStatus());
    final Page<AlertData> page = body(response);
    assertEquals(2, page.getContent().size());
    assertTrue(
      page
        .getContent()
        .stream()
        .allMatch(it -> ownerId.equals(it.recipientId()))
    );
  }

  @Test
  public void testListAlertsDefaultsToFirstPageOfTwentyWhenUnpaged(
    @Given String owner
  ) {
    // Arrange: more rows than the controller's default page size.
    final String ownerId = owner + "-owner";
    IntStream.range(0, 25).forEach(i ->
      repository.save(alert(ownerId + "-" + i, ownerId, BASE.plusSeconds(i)))
    );

    // Act
    final HttpResponse<Page<AlertData>> response = controller.listAlerts(
      AuthenticationGenerator.forUser(ownerId),
      null,
      null,
      Pageable.unpaged()
    );

    // Assert: unpaged requests fall back to page 0, size 20.
    assertEquals(HttpStatus.OK, response.getStatus());
    final Page<AlertData> page = body(response);
    assertEquals(20, page.getContent().size());
    assertEquals(25L, page.getTotalSize());
    assertEquals(0, page.getPageable().getNumber());
    assertEquals(20, page.getPageable().getSize());
  }

  @Test
  public void testListAlertsPaginates(@Given String owner) {
    final String ownerId = owner + "-owner";
    IntStream.range(0, 3).forEach(i ->
      repository.save(alert(ownerId + "-" + i, ownerId, BASE.plusSeconds(i)))
    );

    // Act: first page of two.
    final HttpResponse<Page<AlertData>> response = controller.listAlerts(
      AuthenticationGenerator.forUser(ownerId),
      null,
      null,
      Pageable.from(0, 2)
    );

    // Assert
    assertEquals(HttpStatus.OK, response.getStatus());
    final Page<AlertData> page = body(response);
    assertEquals(2, page.getContent().size());
    assertEquals(3L, page.getTotalSize());
    assertEquals(2, page.getPageable().getSize());
  }

  @Test
  public void testListAlertsFiltersByAfter(@Given String owner) {
    final String ownerId = owner + "-owner";
    repository.save(alert(ownerId + "-1", ownerId, BASE));
    repository.save(alert(ownerId + "-2", ownerId, BASE.plusSeconds(10)));
    repository.save(alert(ownerId + "-3", ownerId, BASE.plusSeconds(20)));

    // Act: strictly after BASE + 10s.
    final HttpResponse<Page<AlertData>> response = controller.listAlerts(
      AuthenticationGenerator.forUser(ownerId),
      BASE.plusSeconds(10),
      null,
      Pageable.unpaged()
    );

    // Assert
    assertEquals(HttpStatus.OK, response.getStatus());
    final Page<AlertData> page = body(response);
    assertEquals(1, page.getContent().size());
    assertEquals(ownerId + "-3", page.getContent().getFirst().id());
  }

  @Test
  public void testListAlertsFiltersByBefore(@Given String owner) {
    final String ownerId = owner + "-owner";
    repository.save(alert(ownerId + "-1", ownerId, BASE));
    repository.save(alert(ownerId + "-2", ownerId, BASE.plusSeconds(10)));
    repository.save(alert(ownerId + "-3", ownerId, BASE.plusSeconds(20)));

    // Act: strictly before BASE + 20s.
    final HttpResponse<Page<AlertData>> response = controller.listAlerts(
      AuthenticationGenerator.forUser(ownerId),
      null,
      BASE.plusSeconds(20),
      Pageable.unpaged()
    );

    // Assert
    assertEquals(HttpStatus.OK, response.getStatus());
    final Page<AlertData> page = body(response);
    assertEquals(2, page.getContent().size());
    assertTrue(
      page
        .getContent()
        .stream()
        .map(AlertData::id)
        .allMatch(id -> id.equals(ownerId + "-1") || id.equals(ownerId + "-2"))
    );
  }

  @Test
  public void testListAlertsFiltersByRange(@Given String owner) {
    final String ownerId = owner + "-owner";
    repository.save(alert(ownerId + "-1", ownerId, BASE));
    repository.save(alert(ownerId + "-2", ownerId, BASE.plusSeconds(10)));
    repository.save(alert(ownerId + "-3", ownerId, BASE.plusSeconds(20)));
    repository.save(alert(ownerId + "-4", ownerId, BASE.plusSeconds(30)));
    repository.save(alert(ownerId + "-5", ownerId, BASE.plusSeconds(40)));

    // Act: exclusive window (BASE + 10s, BASE + 30s).
    final HttpResponse<Page<AlertData>> response = controller.listAlerts(
      AuthenticationGenerator.forUser(ownerId),
      BASE.plusSeconds(10),
      BASE.plusSeconds(30),
      Pageable.unpaged()
    );

    // Assert
    assertEquals(HttpStatus.OK, response.getStatus());
    final Page<AlertData> page = body(response);
    assertEquals(1, page.getContent().size());
    assertEquals(ownerId + "-3", page.getContent().getFirst().id());
  }

  @Test
  public void testListAlertsReturnsUnauthorizedWithoutOwner() {
    // Arrange: authenticated principal without the preferred_username claim.
    final Authentication auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(Map.of());

    // Act
    final HttpResponse<Page<AlertData>> response = controller.listAlerts(
      auth,
      null,
      null,
      Pageable.unpaged()
    );

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
  }

  private static Page<AlertData> body(HttpResponse<Page<AlertData>> response) {
    return Optional.ofNullable(response.body()).orElseThrow();
  }

  private static AlertData alert(
    String id,
    String recipientId,
    Instant createdAt
  ) {
    return new AlertData(
      id,
      recipientId,
      "nickname",
      "message",
      null,
      null,
      "level",
      1,
      createdAt,
      false
    );
  }
}
