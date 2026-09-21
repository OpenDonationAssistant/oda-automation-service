package io.github.opendonationassistant.automation.domain.action;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.EphemeralVariable;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.metrics.AutomationMetrics;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.events.twitch.events.TwitchStreamStartedEvent;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class RestCallActionTest {

  private final HttpClient httpClient = mock(HttpClient.class);
  private final BlockingHttpClient blockingClient = mock(
    BlockingHttpClient.class
  );
  private final AutomationMetrics metrics = new AutomationMetrics(
    new SimpleMeterRegistry()
  );

  @BeforeEach
  public void setUp() {
    when(httpClient.toBlocking()).thenReturn(blockingClient);
  }

  @Test
  public void testSendsRequestWithResolvedUrlHeadersAndPayload() {
    var action = new RestCallAction(
      new AutomationActionData(
        RestCallAction.ID,
        Map.of(
          "url",
          "https://example.com/hooks/<nickname>",
          "method",
          "post",
          "payload",
          "{\"user\":\"<nickname>\",\"token\":\"<token>\"}",
          "headers",
          Map.of("Authorization", "Bearer <token>", "X-Static", "static-value")
        )
      ),
      httpClient
    );
    when(
      blockingClient.exchange(any(HttpRequest.class), eq(String.class))
    ).thenReturn(HttpResponse.ok("{}"));

    action.execute(iterationWith("streamer", "secret"));

    var captor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(blockingClient).exchange(captor.capture(), eq(String.class));
    var request = captor.getValue();
    assertEquals(HttpMethod.POST, request.getMethod());
    assertEquals(
      "https://example.com/hooks/streamer",
      request.getUri().toString()
    );
    assertEquals("Bearer secret", request.getHeaders().get("Authorization"));
    assertEquals("static-value", request.getHeaders().get("X-Static"));
    assertEquals(
      "{\"user\":\"streamer\",\"token\":\"secret\"}",
      request.getBody().map(Object::toString).orElse("")
    );
  }

  @Test
  public void testDefaultsToGetWithoutPayload() {
    var action = new RestCallAction(
      new AutomationActionData(
        RestCallAction.ID,
        Map.of("url", "https://example.com/hooks")
      ),
      httpClient
    );
    when(
      blockingClient.exchange(any(HttpRequest.class), eq(String.class))
    ).thenReturn(HttpResponse.ok("{}"));

    action.execute(iterationWith("streamer", "secret"));

    var captor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(blockingClient).exchange(captor.capture(), eq(String.class));
    assertEquals(HttpMethod.GET, captor.getValue().getMethod());
    assertTrue(captor.getValue().getBody().isEmpty());
  }

  @Test
  public void testSkipsWhenUrlIsMissing() {
    var action = new RestCallAction(
      new AutomationActionData(RestCallAction.ID, Map.of("method", "POST")),
      httpClient
    );

    action.execute(iterationWith("streamer", "secret"));

    verifyNoInteractions(httpClient);
  }

  @Test
  public void testSkipsUnsupportedUrlScheme() {
    var action = new RestCallAction(
      new AutomationActionData(
        RestCallAction.ID,
        Map.of("url", "ftp://example.com/hooks")
      ),
      httpClient
    );

    action.execute(iterationWith("streamer", "secret"));

    verifyNoInteractions(httpClient);
  }

  @Test
  public void testSkipsUnsupportedMethod() {
    var action = new RestCallAction(
      new AutomationActionData(
        RestCallAction.ID,
        Map.of("url", "https://example.com/hooks", "method", "TRACE")
      ),
      httpClient
    );

    action.execute(iterationWith("streamer", "secret"));

    verifyNoInteractions(httpClient);
  }

  @Test
  public void testSwallowsHttpErrors() {
    var action = new RestCallAction(
      new AutomationActionData(
        RestCallAction.ID,
        Map.of("url", "https://example.com/hooks", "method", "POST")
      ),
      httpClient
    );
    when(
      blockingClient.exchange(any(HttpRequest.class), eq(String.class))
    ).thenThrow(
      new HttpClientResponseException(
        "server error",
        HttpResponse.serverError()
      )
    );

    assertDoesNotThrow(() -> action.execute(iterationWith("streamer", "secret"))
    );

    verify(blockingClient).exchange(any(HttpRequest.class), eq(String.class));
  }

  private Iteration iterationWith(String nickname, String token) {
    var iteration = new Iteration(
      "testuser",
      new TwitchStreamStartedEvent("eventId", "testuser", "url"),
      List.of(),
      List.of(),
      metrics
    );
    iteration.add(new EphemeralVariable<>("nickname", nickname));
    iteration.add(new EphemeralVariable<>("token", token));
    return iteration;
  }
}
