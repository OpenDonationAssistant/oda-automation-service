package io.github.opendonationassistant.obsstudio.api;

import io.github.opendonationassistant.obsstudio.dto.ObsSettingsDto;
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
import org.jspecify.annotations.Nullable;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface UpdateObsSettingsApi {
  @Post("/obs-settings/commands/update")
  @Operation(
    summary = "Update OBS Studio settings",
    description = "Creates or updates the OBS Studio settings for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Updated OBS Studio settings",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = ObsSettingsDto.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<ObsSettingsDto> updateObsSettings(
    Authentication auth,
    @Valid @Body UpdateObsSettingsCommand command
  );

  @Serdeable
  public static record UpdateObsSettingsCommand(
    @NotBlank String websocketUrl,
    @Nullable String password
  ) {}
}
