package io.github.opendonationassistant.automation.view;

import io.github.opendonationassistant.automation.api.PanelsApi;
import io.github.opendonationassistant.automation.dto.PanelDto;
import io.github.opendonationassistant.automation.repository.PanelRepository;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import java.util.Optional;

@Controller
@Validated
public class PanelController extends BaseController implements PanelsApi {

  private final PanelRepository panels;

  @Inject
  public PanelController(PanelRepository panels) {
    this.panels = panels;
  }

  @Override
  public HttpResponse<Page<PanelDto>> listPanels(
    Authentication auth,
    Pageable pageable
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    if (pageable.isUnpaged()) {
      pageable = Pageable.from(0, 20);
    }
    return HttpResponse.ok(
      panels
        .listByRecipientId(ownerId.get(), pageable)
        .map(panel -> PanelDto.from(panel.data()))
    );
  }

  @Override
  public HttpResponse<PanelDto> getPanel(Authentication auth, String id) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return panels
      .getByRecipientIdAndId(ownerId.get(), id)
      .map(panel -> PanelDto.from(panel.data()))
      .map(HttpResponse::ok)
      .orElse(HttpResponse.unauthorized());
  }
}
