package io.github.opendonationassistant.automation.commands;

import io.github.opendonationassistant.automation.api.DeletePanelApi;
import io.github.opendonationassistant.automation.repository.PanelRepository;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import java.util.Optional;

@Controller
@Validated
public class DeletePanel extends BaseController implements DeletePanelApi {

  private final PanelRepository panels;

  @Inject
  public DeletePanel(PanelRepository panels) {
    this.panels = panels;
  }

  @Override
  public HttpResponse<Void> deletePanel(
    Authentication auth,
    DeletePanelCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return panels
      .getByRecipientIdAndId(ownerId.get(), command.id())
      .map(panel -> {
        panel.delete();
        return HttpResponse.<Void>ok();
      })
      .orElse(HttpResponse.<Void>unauthorized());
  }
}
