package io.github.opendonationassistant.automation.api;

import io.github.opendonationassistant.automation.commands.SetEnabled.SetEnabledCommand;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface SetEnabledApi {
  @Post("/automation/commands/set-enabled")
  @Operation(
    summary = "Toggle automation rule enabled state",
    description = "Toggles the enabled field for an automation rule by id for the authenticated user"
  )
  @ApiResponse(responseCode = "200", description = "Rule enabled state toggled")
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  @ApiResponse(responseCode = "404", description = "Rule not found")
  HttpResponse<Void> setEnabled(
    Authentication auth,
    @Body SetEnabledCommand command
  );
}
