package io.github.opendonationassistant.wordblacklist.api;

import io.github.opendonationassistant.wordblacklist.WordFilterData;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface GetBlacklistApi {
  @Get("/word-blacklist")
  @Operation(
    summary = "Get word blacklist",
    description = "Retrieves the word blacklist for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Blacklist entries for the user"
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<List<WordFilterData>> getBlacklist(Authentication auth);
}
