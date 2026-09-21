package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RestCallAction extends AutomationAction {

  public static final String ID = "rest-call";

  private static final String DEFAULT_METHOD = "GET";
  private static final Set<String> SUPPORTED_SCHEMES = Set.of("http", "https");

  private final ODALogger log = new ODALogger(this);
  private final HttpClient httpClient;

  public RestCallAction(AutomationActionData data, HttpClient httpClient) {
    super(data);
    this.httpClient = httpClient;
  }

  @Override
  public void execute(Iteration iteration) {
    var url = resolvedUrl(iteration);
    var method = HttpMethod.parse(method());
    if (
      url.isEmpty() ||
      (method != HttpMethod.POST &&
        method != HttpMethod.GET &&
        method != HttpMethod.PUT)
    ) {
      log.warn("Skipping RestCallAction: unsupported url or method", () ->
        Map.of(
          "recipientId",
          iteration.recipientId(),
          "url",
          url.orElse(""),
          "method",
          method()
        )
      );
      return;
    }
    var payload = VariableResolver.resolve(payload(), iteration);
    var headers = resolvedHeaders(iteration);
    log.info("Executing RestCallAction", () ->
      Map.of(
        "recipientId",
        iteration.recipientId(),
        "method",
        method.name(),
        "url",
        url.get()
      )
    );
    try {
      var response = httpClient
        .toBlocking()
        .exchange(
          createRequest(method, url.get(), headers, payload),
          String.class
        );
      log.info("RestCallAction completed", () ->
        Map.of(
          "url",
          url.get(),
          "status",
          Optional.ofNullable(response)
            .map(HttpResponse::getStatus)
            .map(it -> it.getCode())
            .orElse(0)
        )
      );
    } catch (HttpClientException exception) {
      log.error("RestCallAction failed", () ->
        Map.of("url", url.get(), "message", exception.getMessage())
      );
    }
  }

  public Optional<String> resolvedUrl(Iteration iteration) {
    return Optional.ofNullable((String) data().value().get("url"))
      .map(template -> VariableResolver.resolve(template, iteration).trim())
      .filter(RestCallAction::isSupportedUrl);
  }

  public String method() {
    return Optional.ofNullable((String) data().value().get("method"))
      .map(String::toUpperCase)
      .filter(value -> !value.isBlank())
      .orElse(DEFAULT_METHOD);
  }

  public String payload() {
    return Optional.ofNullable((String) data().value().get("payload")).orElse(
      ""
    );
  }

  public Map<CharSequence, CharSequence> resolvedHeaders(Iteration iteration) {
    var raw = data().value().get("headers");
    if (!(raw instanceof Map<?, ?> headers)) {
      return Map.of();
    }
    var resolved = new LinkedHashMap<CharSequence, CharSequence>();
    headers.forEach((name, value) ->
      resolved.put(
        VariableResolver.resolve(String.valueOf(name), iteration),
        VariableResolver.resolve(String.valueOf(value), iteration)
      )
    );
    return resolved;
  }

  private static MutableHttpRequest<?> createRequest(
    HttpMethod method,
    String url,
    Map<CharSequence, CharSequence> headers,
    String payload
  ) {
    var request = HttpRequest.create(method, url).headers(headers);
    if (method.permitsRequestBody() && !payload.isBlank()) {
      return request.body(payload);
    }
    return request;
  }

  private static boolean isSupportedUrl(String url) {
    try {
      var uri = URI.create(url);
      return (
        uri.getHost() != null &&
        uri.getScheme() != null &&
        SUPPORTED_SCHEMES.contains(uri.getScheme().toLowerCase())
      );
    } catch (IllegalArgumentException exception) {
      return false;
    }
  }
}
