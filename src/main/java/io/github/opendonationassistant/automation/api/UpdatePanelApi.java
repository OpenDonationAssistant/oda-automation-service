package io.github.opendonationassistant.automation.api;

import io.github.opendonationassistant.automation.dto.PanelDto.PanelCardDto;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface UpdatePanelApi {
  @Post("/panels/commands/update-panel")
  @Operation(
    summary = "Update panel",
    description = "Updates a panel by id for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Updated panel",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = Void.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  @ApiResponse(responseCode = "404", description = "Panel not found")
  HttpResponse<Void> updatePanel(
    Authentication auth,
    @Valid @Body UpdatePanelCommand command
  );

  @Serdeable
  public static record UpdatePanelCommand(
    @NotBlank String id,
    @NotBlank String name,
    @NotNull @Valid List<PanelCardDto> cards
  ) {}
}
