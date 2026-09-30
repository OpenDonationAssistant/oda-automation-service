package io.github.opendonationassistant.obsstudio.api;

import io.github.opendonationassistant.obsstudio.dto.ObsSettingsDto;
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
public interface GetObsSettingsApi {
  @Get("/obs-settings")
  @Operation(
    summary = "Get OBS Studio settings",
    description = "Retrieves the OBS Studio settings for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "OBS Studio settings for the user",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = ObsSettingsDto.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated or OBS Studio is not configured"
  )
  HttpResponse<ObsSettingsDto> getObsSettings(Authentication auth);
}
