package io.github.opendonationassistant.automation.api;

import io.github.opendonationassistant.automation.dto.PanelDto.PanelCardDto;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface CreatePanelApi {
  @Post("/panels/commands/create-panel")
  @Operation(
    summary = "Create panel",
    description = "Creates a new panel for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Created panel",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = CreatePanelResponse.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<CreatePanelResponse> createPanel(
    Authentication auth,
    @Valid @Body CreatePanelCommand command
  );

  @Serdeable
  public static record CreatePanelResponse(String id) {}

  @Serdeable
  public static record CreatePanelCommand(
    @NotBlank String name,
    @NotNull @Valid List<PanelCardDto> cards
  ) {}
}
