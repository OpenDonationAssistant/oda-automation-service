package io.github.opendonationassistant.automation.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface DeletePanelApi {
  @Post("/panels/commands/delete-panel")
  @Operation(
    summary = "Delete panel",
    description = "Deletes a panel by id for the authenticated user"
  )
  @ApiResponse(responseCode = "200", description = "Panel deleted")
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<Void> deletePanel(
    Authentication auth,
    DeletePanelCommand command
  );

  @Serdeable
  public record DeletePanelCommand(@NotBlank String id) {}
}
