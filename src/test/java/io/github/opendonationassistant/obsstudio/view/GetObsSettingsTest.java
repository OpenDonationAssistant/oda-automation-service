package io.github.opendonationassistant.obsstudio.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

public class GetObsSettingsTest {

  private static final String OWNER_ID = "owner-1";
  private static final String WEBSOCKET_URL = "ws://localhost:4455";
  private static final String PASSWORD = "secret";

  private final ObsSettingsRepository repository = mock(
    ObsSettingsRepository.class
  );
  private final GetObsSettings controller = new GetObsSettings(repository);

  @Test
  public void testGetObsSettingsReturnsSettingsForOwner() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientId(OWNER_ID)).thenReturn(
      Optional.of(settings())
    );

    HttpResponse<ObsSettingsDto> response = controller.getObsSettings(auth);

    assertEquals(HttpStatus.OK, response.getStatus());
    ObsSettingsDto body = Optional.ofNullable(response.body()).orElseThrow();
    assertEquals(WEBSOCKET_URL, body.websocketUrl());
    assertEquals(PASSWORD, body.password());
    verify(repository).getByRecipientId(OWNER_ID);
  }

  @Test
  public void testGetObsSettingsReturnsUnauthorizedWhenAbsent() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientId(OWNER_ID)).thenReturn(Optional.empty());

    // Act
    HttpResponse<ObsSettingsDto> response = controller.getObsSettings(auth);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    verify(repository).getByRecipientId(OWNER_ID);
  }

  @Test
  public void testGetObsSettingsReturnsUnauthorizedWithoutOwner() {
    HttpResponse<ObsSettingsDto> response = controller.getObsSettings(
      unauthenticated()
    );

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    verifyNoInteractions(repository);
  }

  private static Authentication unauthenticated() {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(Map.of());
    return auth;
  }

  private static ObsSettingsData settings() {
    return new ObsSettingsData("settings-1", OWNER_ID, WEBSOCKET_URL, PASSWORD);
  }
}
