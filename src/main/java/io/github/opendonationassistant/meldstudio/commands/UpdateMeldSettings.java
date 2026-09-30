package io.github.opendonationassistant.meldstudio.commands;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.meldstudio.api.UpdateMeldSettingsApi;
import io.github.opendonationassistant.meldstudio.dto.MeldSettingsDto;
import io.github.opendonationassistant.meldstudio.repository.MeldSettingsRepository;
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
public class UpdateMeldSettings extends BaseController
  implements UpdateMeldSettingsApi {

  private final MeldSettingsRepository repository;

  @Inject
  public UpdateMeldSettings(MeldSettingsRepository repository) {
    this.repository = repository;
  }

  @Override
  public HttpResponse<MeldSettingsDto> updateMeldSettings(
    Authentication auth,
    @Valid @Body UpdateMeldSettingsCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.ok(
      MeldSettingsDto.from(
        repository.upsert(ownerId.get(), command.websocketUrl())
      )
    );
  }
}
