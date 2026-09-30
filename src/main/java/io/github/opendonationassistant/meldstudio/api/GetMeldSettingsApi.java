package io.github.opendonationassistant.meldstudio.api;

import io.github.opendonationassistant.meldstudio.dto.MeldSettingsDto;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface GetMeldSettingsApi {
  @Get("/meld-settings")
  @Operation(
    summary = "Get Meld Studio settings",
    description = "Retrieves Meld Studio settings for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Meld Studio settings for the user",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = MeldSettingsDto.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated. Also would be returned if Meld Studio is not configured"
  )
  HttpResponse<MeldSettingsDto> getMeldSettings(Authentication auth);
}
