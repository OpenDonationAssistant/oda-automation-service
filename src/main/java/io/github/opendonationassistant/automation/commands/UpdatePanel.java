package io.github.opendonationassistant.automation.commands;

import io.github.opendonationassistant.automation.api.UpdatePanelApi;
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
public class UpdatePanel extends BaseController implements UpdatePanelApi {

  private final PanelRepository panels;

  @Inject
  public UpdatePanel(PanelRepository panels) {
    this.panels = panels;
  }

  @Override
  public HttpResponse<Void> updatePanel(
    Authentication auth,
    @Valid @Body UpdatePanelCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return panels
      .getByRecipientIdAndId(ownerId.get(), command.id())
      .map(panel -> {
        panel.updateNameAndCards(
          command.name(),
          command.cards().stream().map(PanelCardDto::toData).toList()
        );
        return HttpResponse.<Void>ok();
      })
      .orElse(HttpResponse.<Void>notFound());
  }
}
