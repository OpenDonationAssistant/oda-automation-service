package io.github.opendonationassistant.automation.api;

import java.util.Map;

import org.jspecify.annotations.Nullable;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Secured(SecurityRule.IS_AUTHENTICATED)
public interface TriggerRuleApi {
  @Post("/automation/commands/trigger-rule")
  @Operation(
    summary = "Trigger automation rule by command",
    description = "Runs an iteration for the automation rule with the given id, using the command as the trigger source"
  )
  @ApiResponse(responseCode = "200", description = "Rule triggered")
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - user not authenticated"
  )
  @ApiResponse(responseCode = "404", description = "Rule not found")
  HttpResponse<Void> triggerRule(
    Authentication auth,
    @Valid @Body TriggerRuleCommand command
  );

  @Serdeable
  public record TriggerRuleCommand(
    @NotBlank String id,
    @Nullable String nickname,
    @Nullable String system,
    @Nullable Map<String, String> variables
  ) {}
}

