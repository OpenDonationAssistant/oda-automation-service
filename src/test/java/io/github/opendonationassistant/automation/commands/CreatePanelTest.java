package io.github.opendonationassistant.automation.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.automation.api.CreatePanelApi;
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

public class CreatePanelTest {

  private static final String OWNER_ID = "owner-1";
  private static final String PANEL_ID = "panel-1";

  private final PanelDataRepository dataRepository = mock(
    PanelDataRepository.class
  );
  private final PanelRepository repository = mock(PanelRepository.class);
  private final CreatePanel controller = new CreatePanel(repository);

  @Test
  public void testCreatePanelReturnsCreated() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.create(any(), any(), any())).thenAnswer(invocation ->
      panel(PANEL_ID, invocation.getArgument(1))
    );
    var cards = List.of(new PanelCardDto("card-1", "rule-1", "Card title"));
    var name = "New panel";
    var command = new CreatePanelApi.CreatePanelCommand(name, cards);

    HttpResponse<CreatePanelApi.CreatePanelResponse> response =
      controller.createPanel(auth, command);

    assertEquals(HttpStatus.CREATED, response.getStatus());
    CreatePanelApi.CreatePanelResponse body = Optional.ofNullable(
      response.body()
    ).orElseThrow();
    assertNotNull(body.id());

    verify(repository).create(
      eq(OWNER_ID),
      eq(name),
      eq(List.of(new PanelCardData("card-1", "rule-1", "Card title")))
    );
  }

  @Test
  public void testReturnUnauthorizedWithoutOwner() {
    var createCommand = new CreatePanelApi.CreatePanelCommand(
      "panel",
      List.of()
    );
    assertEquals(
      HttpStatus.UNAUTHORIZED,
      controller.createPanel(unauthenticated(), createCommand).getStatus()
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
