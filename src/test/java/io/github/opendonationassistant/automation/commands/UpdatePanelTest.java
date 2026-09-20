package io.github.opendonationassistant.automation.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.automation.api.UpdatePanelApi;
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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class UpdatePanelTest {

  private static final String OWNER_ID = "owner-1";
  private static final String PANEL_ID = "panel-1";
  private static final String PANEL_NAME = "Stream panel";

  private final PanelDataRepository dataRepository = mock(
    PanelDataRepository.class
  );
  private final PanelRepository repository = mock(PanelRepository.class);
  private final UpdatePanel controller = new UpdatePanel(repository);

  @Test
  public void testUpdatePanelReturnsNotFoundForMissingPanel() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientIdAndId(OWNER_ID, PANEL_ID)).thenReturn(
      Optional.empty()
    );
    var command = new UpdatePanelApi.UpdatePanelCommand(
      PANEL_ID,
      "Renamed panel",
      List.of()
    );

    HttpResponse<Void> response = controller.updatePanel(auth, command);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
  }

  @Test
  public void testUpdatePanelUpdatesExistingPanel() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientIdAndId(OWNER_ID, PANEL_ID)).thenReturn(
      Optional.of(panel(PANEL_ID, PANEL_NAME))
    );
    var command = new UpdatePanelApi.UpdatePanelCommand(
      PANEL_ID,
      "Renamed panel",
      List.of(new PanelCardDto("card-9", "rule-9", "Updated"))
    );

    HttpResponse<Void> response = controller.updatePanel(auth, command);

    assertEquals(HttpStatus.OK, response.getStatus());

    ArgumentCaptor<PanelData> captor = ArgumentCaptor.forClass(PanelData.class);
    verify(dataRepository).update(captor.capture());

    PanelData updated = captor.getValue();
    assertEquals(PANEL_ID, updated.id());
    assertEquals("Renamed panel", updated.name());
    assertEquals(1, updated.cards().size());
    assertEquals("card-9", updated.cards().get(0).id());
  }

  @Test
  public void testReturnUnauthorizedWithoutOwner() {
    var updateCommand = new UpdatePanelApi.UpdatePanelCommand(
      PANEL_ID,
      "panel",
      List.of()
    );
    assertEquals(
      HttpStatus.UNAUTHORIZED,
      controller.updatePanel(unauthenticated(), updateCommand).getStatus()
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
