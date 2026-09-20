package io.github.opendonationassistant.automation.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.api.CreatePanelApi;
import io.github.opendonationassistant.automation.api.UpdatePanelApi;
import io.github.opendonationassistant.automation.dto.PanelDto;
import io.github.opendonationassistant.automation.dto.PanelDto.PanelCardDto;
import io.github.opendonationassistant.automation.repository.Panel;
import io.github.opendonationassistant.automation.repository.PanelData;
import io.github.opendonationassistant.automation.repository.PanelData.PanelCardData;
import io.github.opendonationassistant.automation.repository.PanelDataRepository;
import io.github.opendonationassistant.automation.repository.PanelRepository;
import io.github.opendonationassistant.testutils.AuthenticationGenerator;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.Authentication;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PanelController} with a mocked {@link PanelRepository}.
 *
 * <p>The owner is derived from the {@code preferred_username} claim. When the
 * claim is absent every operation must return 401 and must not touch the
 * repository.
 */
public class PanelControllerTest {

  private static final String OWNER_ID = "owner-1";
  private static final String PANEL_ID = "panel-1";
  private static final String PANEL_NAME = "Stream panel";

  private final PanelDataRepository dataRepository = mock(
    PanelDataRepository.class
  );
  private final PanelRepository repository = mock(PanelRepository.class);
  private final PanelController controller = new PanelController(repository);

  @Test
  public void testGetPanelReturnsPanelForOwner() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientIdAndId(any(), any())).thenReturn(
      Optional.of(panel(PANEL_ID, PANEL_NAME))
    );

    HttpResponse<PanelDto> response = controller.getPanel(auth, PANEL_ID);

    assertEquals(HttpStatus.OK, response.getStatus());
    PanelDto body = Optional.ofNullable(response.body()).orElseThrow();
    assertEquals(PANEL_ID, body.id());
    assertEquals(PANEL_NAME, body.name());
    assertEquals(1, body.cards().size());
    PanelCardDto card = body.cards().get(0);
    assertEquals("card-1", card.id());
    assertEquals("rule-1", card.ruleId());
    assertEquals("Card title", card.title());
    verify(repository).getByRecipientIdAndId(OWNER_ID, PANEL_ID);
  }

  @Test
  public void testGetPanelReturnsUnauthorizedForMissingPanel() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientIdAndId(OWNER_ID, PANEL_ID)).thenReturn(
      Optional.empty()
    );

    HttpResponse<PanelDto> response = controller.getPanel(auth, PANEL_ID);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    verify(repository).getByRecipientIdAndId(OWNER_ID, PANEL_ID);
  }

  @Test
  public void testListPanelsReturnsPanelsForOwner() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.listByRecipientId(any())).thenReturn(
      Stream.of(panel(PANEL_ID, PANEL_NAME))
    );

    HttpResponse<List<PanelDto>> response = controller.listPanels(auth);

    assertEquals(HttpStatus.OK, response.getStatus());
    List<PanelDto> body = Optional.ofNullable(response.body()).orElseThrow();
    assertEquals(1, body.size());
    assertEquals(PANEL_ID, body.get(0).id());
    verify(repository).listByRecipientId(OWNER_ID);
  }

  @Test
  public void testAllEndpointsReturnUnauthorizedWithoutOwner() {
    assertEquals(
      HttpStatus.UNAUTHORIZED,
      controller.listPanels(unauthenticated()).getStatus()
    );
    assertEquals(
      HttpStatus.UNAUTHORIZED,
      controller.getPanel(unauthenticated(), PANEL_ID).getStatus()
    );

    verifyNoInteractions(repository);
  }

  private static Authentication unauthenticated() {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(Map.of());
    return auth;
  }

  private Panel panel(String id, String name) {
    return new Panel(panelData(id, name), dataRepository);
  }

  private static PanelData panelData(String id, String name) {
    return new PanelData(
      id,
      name,
      OWNER_ID,
      List.of(new PanelCardData("card-1", "rule-1", "Card title"))
    );
  }
}
