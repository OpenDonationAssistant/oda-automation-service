package io.github.opendonationassistant.automation.api;

import io.github.opendonationassistant.automation.dto.AutomationDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(
  name = "Automation",
  description = "Automation operations for managing rules and variables"
)
public interface AutomationOperationsApi {
  @Get("/automation/variables")
  @Operation(
    summary = "List automation variables",
    description = "Retrieves paginated automation variables for the authenticated user",
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
    description = "Paginated list of automation variables",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = ListVariablesResponse.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<Page<AutomationVariableDto>> listVariables(
    Authentication auth,
    Pageable pageable
  );

  @Get("/automation/rules")
  @Operation(
    summary = "List automation rules",
    description = "Retrieves paginated automation rules for the authenticated user",
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
    description = "Paginated list of automation rules",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = ListAutomationsResponse.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<Page<AutomationRuleDto>> listAutomations(
    Authentication auth,
    Pageable pageable
  );

  @Get("/automation/")
  @Operation(
    summary = "Get automation state",
    description = "Retrieves the complete automation state including all rules and variables"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Complete automation state",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = AutomationDto.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<AutomationDto> getState(Authentication auth);

  @Serdeable
  public static interface ListVariablesResponse
    extends Page<AutomationVariableDto> {}

  @Serdeable
  public static interface ListAutomationsResponse
    extends Page<AutomationRuleDto> {}

  default Optional<String> getOwnerId(Authentication auth) {
    return Optional.ofNullable(
      String.valueOf(auth.getAttributes().get("preferred_username"))
    );
  }
}

