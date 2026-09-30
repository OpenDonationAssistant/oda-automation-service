package io.github.opendonationassistant.meldstudio.view;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.meldstudio.api.GetMeldSettingsApi;
import io.github.opendonationassistant.meldstudio.dto.MeldSettingsDto;
import io.github.opendonationassistant.meldstudio.repository.MeldSettingsRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import java.util.Optional;

@Controller
@Validated
public class GetMeldSettings
  extends BaseController
  implements GetMeldSettingsApi {

  private final MeldSettingsRepository repository;

  @Inject
  public GetMeldSettings(MeldSettingsRepository repository) {
    this.repository = repository;
  }

  @Override
  public HttpResponse<MeldSettingsDto> getMeldSettings(Authentication auth) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return repository
      .getByRecipientId(ownerId.get())
      .map(MeldSettingsDto::from)
      .map(HttpResponse::ok)
      .orElse(HttpResponse.<MeldSettingsDto>unauthorized());
  }
}
