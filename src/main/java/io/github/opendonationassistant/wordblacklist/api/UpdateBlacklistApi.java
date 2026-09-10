package io.github.opendonationassistant.wordblacklist.api;

import io.github.opendonationassistant.wordblacklist.WordFilterData;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface UpdateBlacklistApi {
  @Post("/word-blacklist/commands/update")
  @Operation(
    summary = "Update word blacklist",
    description = "Creates or updates the word blacklist for the authenticated user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Blacklist updated successfully"
  )
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  HttpResponse<WordFilterData> updateBlacklist(
    Authentication auth,
    @Valid @Body List<String> words
  );
}
