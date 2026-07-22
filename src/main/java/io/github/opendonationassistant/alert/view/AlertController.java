package io.github.opendonationassistant.alert.view;

import static io.micronaut.data.repository.jpa.criteria.PredicateSpecification.*;

import io.github.opendonationassistant.alert.api.GetAlertsApi;
import io.github.opendonationassistant.alert.repository.AlertData;
import io.github.opendonationassistant.alert.repository.AlertDataRepository;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.authentication.Authentication;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@Controller
public class AlertController extends BaseController implements GetAlertsApi {

  private final AlertDataRepository repository;

  public AlertController(AlertDataRepository repository) {
    this.repository = repository;
  }

  @Override
  public HttpResponse<Page<AlertData>> listAlerts(
    Authentication auth,
    @Nullable Instant after,
    @Nullable Instant before,
    Pageable pageable
  ) {
    Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }

    if (pageable.isUnpaged()) {
      pageable = Pageable.from(0, 20);
    }

    final ArrayList<PredicateSpecification<AlertData>> conditions =
      new ArrayList<>();
    conditions.add(
      where((root, builder) ->
        builder.equal(root.get("recipientId"), ownerId.get())
      )
    );
    if (after != null) {
      conditions.add(
        where((root, builder) ->
          builder.greaterThan(root.get("timestamp"), after)
        )
      );
    }
    if (before != null) {
      conditions.add(
        where((root, builder) -> builder.lessThan(root.get("timestamp"), before)
        )
      );
    }

    return HttpResponse.ok(
      repository.findAll(
        (root, builder) -> {
          return builder.and(
            conditions
              .stream()
              .map(it -> it.toPredicate(root, builder))
              .toArray(Predicate[]::new)
          );
        },
        pageable
      )
    );
  }
}
