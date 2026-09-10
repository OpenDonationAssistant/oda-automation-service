package io.github.opendonationassistant.wordblacklist.commands;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.wordblacklist.WordFilterData;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import io.github.opendonationassistant.wordblacklist.api.UpdateBlacklistApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@Validated
public class UpdateBlacklist
  extends BaseController
  implements UpdateBlacklistApi {

  private final WordFilterRepository repository;

  @Inject
  public UpdateBlacklist(WordFilterRepository repository) {
    this.repository = repository;
  }

  @Override
  public HttpResponse<WordFilterData> updateBlacklist(
    Authentication auth,
    @Valid @Body UpdateBlacklistCommand command
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    return ownerId
      .map(repository::getByRecipientId)
      .map(filter -> filter.replaceWords(command.words()).save())
      .map(HttpResponse::ok)
      .orElse(HttpResponse.unauthorized());
  }
}
