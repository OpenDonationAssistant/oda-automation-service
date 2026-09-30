package io.github.opendonationassistant.meldstudio.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.meldstudio.api.UpdateMeldSettingsApi;
import io.github.opendonationassistant.meldstudio.dto.MeldSettingsDto;
import io.github.opendonationassistant.meldstudio.repository.MeldSettingsData;
import io.github.opendonationassistant.meldstudio.repository.MeldSettingsRepository;
import io.github.opendonationassistant.testutils.AuthenticationGenerator;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.Authentication;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UpdateMeldSettings} with a mocked
 * {@link MeldSettingsRepository}.
 *
 * <p>Updates are owner-scoped upserts: the first call creates a row, a later
 * call reuses the existing id. Without an owner claim the endpoint responds
 * 401 and never calls the repository.
 */
public class UpdateMeldSettingsTest {

  private static final String OWNER_ID = "owner-1";
  private static final String EXISTING_ID = "existing-1";
  private static final String WEBSOCKET_URL = "ws://localhost:1234/meld";

  private final MeldSettingsRepository repository = mock(
    MeldSettingsRepository.class
  );
  private final UpdateMeldSettings controller = new UpdateMeldSettings(
    repository
  );

  @Test
  public void testUpdateMeldSettingsCreatesWhenAbsent() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.upsert(any(), any())).thenReturn(
      new MeldSettingsData(EXISTING_ID, OWNER_ID, WEBSOCKET_URL)
    );
    var command = new UpdateMeldSettingsApi.UpdateMeldSettingsCommand(
      WEBSOCKET_URL
    );

    HttpResponse<MeldSettingsDto> response =
      controller.updateMeldSettings(auth, command);

    assertEquals(HttpStatus.OK, response.getStatus());
    MeldSettingsDto body = Optional.ofNullable(response.body()).orElseThrow();
    assertEquals(WEBSOCKET_URL, body.websocketUrl());
    verify(repository).upsert(eq(OWNER_ID), eq(WEBSOCKET_URL));
  }

  @Test
  public void testUpdateMeldSettingsReturnsUnauthorizedWithoutOwner() {
    var command = new UpdateMeldSettingsApi.UpdateMeldSettingsCommand(
      WEBSOCKET_URL
    );

    HttpResponse<MeldSettingsDto> response = controller.updateMeldSettings(
      unauthenticated(),
      command
    );

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    verifyNoInteractions(repository);
  }

  private static Authentication unauthenticated() {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(Map.of());
    return auth;
  }
}
