package io.github.opendonationassistant.automation.api;

import io.github.opendonationassistant.automation.dto.PanelDto;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface PanelsApi {
  @Get("/panels")
  @Operation(
    summary = "List panels",
    description = "Retrieves paginated panels for the authenticated user",
    parameters = {
      @Parameter(
        name = "page",
        in = ParameterIn.QUERY,
        description = "Zero-indexed page number (0-based)",
        required = false,
        schema = @Schema(
          implementation = Integer.class,
          type = "integer",
          format = "int32",
          example = "0",
          minimum = "0"
        )
      ),
      @Parameter(
        name = "size",
        in = ParameterIn.QUERY,
        schema = @Schema(implementation = Integer.class),
        description = "Number of items per page"
      ),
      @Parameter(
        name = "sort",
        schema = @Schema(implementation = String.class),
        in = ParameterIn.QUERY,
        description = "Sorting criteria in format: property,asc|desc"
      ),
    }
  )
  @ApiResponse(
    responseCode = "200",
    description = "Paginated list of panels",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PanelsApi.ListPanelsResponse.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<Page<PanelDto>> listPanels(
    Authentication auth,
    Pageable pageable
  );

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

  @Serdeable
  public static interface ListPanelsResponse extends Page<PanelDto> {}
}
