package io.github.opendonationassistant.alert.api;

import io.github.opendonationassistant.alert.repository.AlertData;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface GetAlertsApi {
  @Get("/alerts")
  @Operation(
    summary = "List alerts",
    description = "Retrieves paginated alerts for the authenticated user with optional filters"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Paginated list of alerts",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = GetAlertsApiResponse.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<Page<AlertData>> listAlerts(
    Authentication auth,
    @Nullable @QueryValue("after") Instant after,
    @Nullable @QueryValue("before") Instant before,
    Pageable pageable
  );

  @Serdeable
  public static interface GetAlertsApiResponse extends Page<AlertData> {}
}
