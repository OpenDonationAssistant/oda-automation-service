package io.github.opendonationassistant.automation.api;

import io.github.opendonationassistant.automation.dto.PanelDto;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface PanelsApi {
  @Get("/panels")
  @Operation(
    summary = "List panels",
    description = "Retrieves all panels for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "List of panels",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PanelDto[].class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<List<PanelDto>> listPanels(Authentication auth);

  @Get("/panels/{id}")
  @Operation(
    summary = "Get panel",
    description = "Retrieves a panel by id for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Panel",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PanelDto.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  @ApiResponse(responseCode = "404", description = "Panel not found")
  HttpResponse<PanelDto> getPanel(Authentication auth, String id);
}
