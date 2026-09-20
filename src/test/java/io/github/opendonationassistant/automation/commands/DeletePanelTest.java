package io.github.opendonationassistant.automation.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.automation.api.DeletePanelApi.DeletePanelCommand;
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
import org.junit.jupiter.api.Test;

public class DeletePanelTest {

  private static final String OWNER_ID = "owner-1";
  private static final String PANEL_ID = "panel-1";
  private static final String PANEL_NAME = "Stream panel";

  private final PanelDataRepository dataRepository = mock(
    PanelDataRepository.class
  );
  private final PanelRepository repository = mock(PanelRepository.class);
  private final DeletePanel controller = new DeletePanel(repository);

  @Test
  public void testDeletePanelDeletesOwnedPanel() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientIdAndId(any(), any())).thenReturn(
      Optional.of(panel(PANEL_ID, PANEL_NAME))
    );

    HttpResponse<Void> response = controller.deletePanel(
      auth,
      new DeletePanelCommand(PANEL_ID)
    );

    assertEquals(HttpStatus.OK, response.getStatus());
    verify(repository).getByRecipientIdAndId(OWNER_ID, PANEL_ID);
    verify(dataRepository).deleteById(PANEL_ID);
  }

  @Test
  public void testDeletePanelIsIdempotentForMissingPanel() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientIdAndId(any(), any())).thenReturn(
      Optional.empty()
    );

    HttpResponse<Void> response = controller.deletePanel(
      auth,
      new DeletePanelCommand(PANEL_ID)
    );

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    verify(dataRepository, never()).deleteById(PANEL_ID);
  }

  @Test
  public void testAllEndpointsReturnUnauthorizedWithoutOwner() {
    assertEquals(
      HttpStatus.UNAUTHORIZED,
      controller
        .deletePanel(unauthenticated(), new DeletePanelCommand(PANEL_ID))
        .getStatus()
    );

    verifyNoInteractions(repository);
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

  private static Authentication unauthenticated() {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(Map.of());
    return auth;
  }
}
