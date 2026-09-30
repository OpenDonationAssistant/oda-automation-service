package io.github.opendonationassistant.meldstudio.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

public class GetMeldSettingsTest {

  private static final String OWNER_ID = "owner-1";
  private static final String SETTINGS_ID = "settings-1";
  private static final String WEBSOCKET_URL = "ws://localhost:1234/meld";

  private final MeldSettingsRepository repository = mock(
    MeldSettingsRepository.class
  );
  private final GetMeldSettings controller = new GetMeldSettings(repository);

  @Test
  public void testGetMeldSettingsReturnsSettingsForOwner() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientId(any())).thenReturn(
      Optional.of(settings(SETTINGS_ID, WEBSOCKET_URL))
    );

    HttpResponse<MeldSettingsDto> response = controller.getMeldSettings(auth);

    assertEquals(HttpStatus.OK, response.getStatus());
    MeldSettingsDto body = Optional.ofNullable(response.body()).orElseThrow();
    assertEquals(WEBSOCKET_URL, body.websocketUrl());
    verify(repository).getByRecipientId(OWNER_ID);
  }

  @Test
  public void testGetMeldSettingsReturnsUnauthorizedWhenAbsent() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(repository.getByRecipientId(any())).thenReturn(Optional.empty());

    HttpResponse<MeldSettingsDto> response = controller.getMeldSettings(auth);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    verify(repository).getByRecipientId(OWNER_ID);
  }

  @Test
  public void testGetMeldSettingsReturnsUnauthorizedWithoutOwner() {
    HttpResponse<MeldSettingsDto> response = controller.getMeldSettings(
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

  private static MeldSettingsData settings(String id, String websocketUrl) {
    return new MeldSettingsData(id, OWNER_ID, websocketUrl);
  }
}
