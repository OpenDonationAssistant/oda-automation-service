package io.github.opendonationassistant.automation.api;

import io.github.opendonationassistant.automation.dto.AutomationDto;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.dto.AutomationVariableDto;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "Automation", description = "Automation operations for managing rules and variables")
public interface AutomationOperationsApi {

  @Get("/automation/variables")
  @Operation(
    summary = "List automation variables",
    description = "Retrieves all automation variables for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "List of automation variables",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = AutomationVariableDto[].class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<List<AutomationVariableDto>> listVariables(Authentication auth);

  @Get("/automation/rules")
  @Operation(
    summary = "List automation rules",
    description = "Retrieves all automation rules for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "List of automation rules",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = AutomationRuleDto[].class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<List<AutomationRuleDto>> listAutomations(Authentication auth);

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

  default Optional<String> getOwnerId(Authentication auth) {
    return Optional.ofNullable(
      String.valueOf(auth.getAttributes().get("preferred_username"))
    );
  }
}
