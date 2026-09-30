package io.github.opendonationassistant.obsstudio.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.obsstudio.api.UpdateObsSettingsApi.UpdateObsSettingsCommand;
import io.github.opendonationassistant.obsstudio.dto.ObsSettingsDto;
import io.github.opendonationassistant.obsstudio.repository.ObsSettingsData;
import io.github.opendonationassistant.obsstudio.repository.ObsSettingsRepository;
import io.github.opendonationassistant.testutils.AuthenticationGenerator;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.Authentication;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class UpdateObsSettingsTest {

  private static final String OWNER_ID = "owner-1";
  private static final String SETTINGS_ID = "settings-1";
  private static final String WEBSOCKET_URL = "ws://localhost:4455";
  private static final String PASSWORD = "secret";
  private static final String UPDATED_WEBSOCKET_URL = "ws://localhost:4466";
  private static final String UPDATED_PASSWORD = "new-secret";

  private final ObsSettingsRepository repository = mock(
    ObsSettingsRepository.class
  );
  private final UpdateObsSettings controller = new UpdateObsSettings(repository);

  @Test
  public void testUpdateObsSettingsCreatesWhenAbsent() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(
      repository.upsert(any(), any(), any())
    ).thenReturn(settings(SETTINGS_ID, WEBSOCKET_URL, PASSWORD));
    var command = new UpdateObsSettingsCommand(WEBSOCKET_URL, PASSWORD);

    HttpResponse<ObsSettingsDto> response = controller.updateObsSettings(
      auth,
      command
    );

    assertEquals(HttpStatus.OK, response.getStatus());
    ObsSettingsDto body = Optional.ofNullable(response.body()).orElseThrow();
    assertEquals(WEBSOCKET_URL, body.websocketUrl());
    assertEquals(PASSWORD, body.password());
    verify(repository).upsert(OWNER_ID, WEBSOCKET_URL, PASSWORD);
  }

  @Test
  public void testUpdateObsSettingsReturnsUnauthorizedWithoutOwner() {
    var command = new UpdateObsSettingsCommand(WEBSOCKET_URL, PASSWORD);

    HttpResponse<ObsSettingsDto> response = controller.updateObsSettings(
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

  private static ObsSettingsData settings(
    String id,
    String websocketUrl,
    String password
  ) {
    return new ObsSettingsData(id, OWNER_ID, websocketUrl, password);
  }
}
