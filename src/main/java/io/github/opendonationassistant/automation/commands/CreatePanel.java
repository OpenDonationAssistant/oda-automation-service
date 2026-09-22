package io.github.opendonationassistant.automation.commands;

import io.github.opendonationassistant.automation.api.CreatePanelApi;
import io.github.opendonationassistant.automation.dto.PanelDto.PanelCardDto;
import io.github.opendonationassistant.automation.repository.PanelRepository;
import io.github.opendonationassistant.commons.micronaut.BaseController;
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
public class CreatePanel extends BaseController implements CreatePanelApi {

  private final PanelRepository panels;

  @Inject
  public CreatePanel(PanelRepository panels) {
    this.panels = panels;
  }

  @Override
  public HttpResponse<CreatePanelResponse> createPanel(
    Authentication auth,
    @Valid @Body CreatePanelCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.created(
      new CreatePanelResponse(
        panels
          .create(
            ownerId.get(),
            command.name(),
            command.cards().stream().map(PanelCardDto::toData).toList()
          )
          .data()
          .id()
      )
    );
  }
}
