package io.github.opendonationassistant.automation.api;

import io.github.opendonationassistant.automation.commands.SetState.SetStateCommand;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface SetStateApi {
  @Post("/automation/commands/setstate")
  @Operation(
    summary = "Set automation state",
    description = "Updates automation variables and rules for the authenticated user"
  )
  @ApiResponse(responseCode = "200", description = "State successfully updated")
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<Void> setState(
    Authentication auth,
    @Body SetStateCommand command
  );
}
