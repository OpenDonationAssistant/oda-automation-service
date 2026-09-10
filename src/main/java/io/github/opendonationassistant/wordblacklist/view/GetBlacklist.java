package io.github.opendonationassistant.wordblacklist.view;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.wordblacklist.WordFilterData;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import io.github.opendonationassistant.wordblacklist.api.GetBlacklistApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@Controller
@Validated
public class GetBlacklist extends BaseController implements GetBlacklistApi {

  private final WordFilterRepository repository;

  @Inject
  public GetBlacklist(WordFilterRepository repository) {
    this.repository = repository;
  }

  @Override
  public HttpResponse<List<WordFilterData>> getBlacklist(Authentication auth) {
    Optional<String> ownerId = getOwnerId(auth);
    return ownerId
      .map(repository::getWordsByRecipientId)
      .map(HttpResponse::ok)
      .orElse(HttpResponse.unauthorized());
  }
}
