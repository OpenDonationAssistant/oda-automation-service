package io.github.opendonationassistant.automation.api;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;
import java.util.concurrent.CompletableFuture;

@Client(id = "widgets")
@Header(name = CONTENT_TYPE, value = "application/json")
public interface WidgetsApi {

  @Get("/admin/widgets/{id}")
  public CompletableFuture<Widget> getWidget(@PathVariable String id);
}
