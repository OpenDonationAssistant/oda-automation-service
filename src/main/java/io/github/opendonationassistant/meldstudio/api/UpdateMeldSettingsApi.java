package io.github.opendonationassistant.meldstudio.api;

import io.github.opendonationassistant.meldstudio.dto.MeldSettingsDto;
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

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface UpdateMeldSettingsApi {
  @Post("/meld-settings/commands/update")
  @Operation(
    summary = "Update Meld Studio settings",
    description = "Creates or updates Meld Studio settings for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Meld Studio settings updated successfully",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = MeldSettingsDto.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<MeldSettingsDto> updateMeldSettings(
    Authentication auth,
    @Valid @Body UpdateMeldSettingsCommand command
  );

  @Serdeable
  public static record UpdateMeldSettingsCommand(@NotBlank String websocketUrl) {}
}
