package io.github.opendonationassistant.obsstudio.commands;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.obsstudio.api.UpdateObsSettingsApi;
import io.github.opendonationassistant.obsstudio.dto.ObsSettingsDto;
import io.github.opendonationassistant.obsstudio.repository.ObsSettingsRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@Validated
public class UpdateObsSettings extends BaseController
  implements UpdateObsSettingsApi {

  private final ObsSettingsRepository repository;

  @Inject
  public UpdateObsSettings(ObsSettingsRepository repository) {
    this.repository = repository;
  }

  @Override
  public HttpResponse<ObsSettingsDto> updateObsSettings(
    Authentication auth,
    @Valid @Body UpdateObsSettingsCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.ok(
      ObsSettingsDto.from(
        repository.upsert(ownerId.get(), command.websocketUrl(), command.password())
      )
    );
  }
}
