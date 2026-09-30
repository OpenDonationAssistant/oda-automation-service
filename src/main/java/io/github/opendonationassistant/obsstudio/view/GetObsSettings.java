package io.github.opendonationassistant.obsstudio.view;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.obsstudio.api.GetObsSettingsApi;
import io.github.opendonationassistant.obsstudio.dto.ObsSettingsDto;
import io.github.opendonationassistant.obsstudio.repository.ObsSettingsRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import java.util.Optional;

@Controller
@Validated
public class GetObsSettings extends BaseController
  implements GetObsSettingsApi {

  private final ObsSettingsRepository repository;

  @Inject
  public GetObsSettings(ObsSettingsRepository repository) {
    this.repository = repository;
  }

  @Override
  public HttpResponse<ObsSettingsDto> getObsSettings(Authentication auth) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return repository
      .getByRecipientId(ownerId.get())
      .map(ObsSettingsDto::from)
      .map(HttpResponse::ok)
      .orElse(HttpResponse.<ObsSettingsDto>notFound());
  }
}
