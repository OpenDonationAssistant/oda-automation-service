package io.github.opendonationassistant;

import io.github.opendonationassistant.rabbit.AMQPConfiguration;
import io.github.opendonationassistant.rabbit.Exchange;
import io.github.opendonationassistant.rabbit.Queue;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.ApplicationContextConfigurer;
import io.micronaut.context.annotation.ContextConfigurer;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.rabbitmq.connect.ChannelInitializer;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;

@OpenAPIDefinition(
  info = @Info(
    title = "ODA Automation Service",
    version = "1.0.0",
    description = "ODA Automation Service",
    license = @License(
      name = "GPL-3.0",
      url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
    ),
    contact = @Contact(name = "stCarolas", email = "stcarolas@gmail.com")
  )
)
@Factory
public class Application {

  @ContextConfigurer
  public static class Configurer implements ApplicationContextConfigurer {

    @Override
    public void configure(@NonNull ApplicationContextBuilder builder) {
      builder.defaultEnvironments("standalone");
    }
  }

  public static void main(String[] args) {
    Micronaut.build(args).banner(false).classes(Application.class).start();
  }

  @Singleton
  public ChannelInitializer rabbitConfiguration() {
    var events = new Queue("automation.events");
    return new AMQPConfiguration(
      List.of(
        Exchange.Exchange(
          "history",
          Map.of(
            "event.MediaHistoryEvent",
            events,
            "event.ReelResultHistoryEvent",
            events,
            "event.HistoryItemEvent",
            events,
            "event.GoalHistoryEvent",
            events,
            "event.CreateAlertCommand",
            events
          )
        ),
        Exchange.Exchange("payments", Map.of("event.PaymentEvent", events)),
        Exchange.Exchange("automation", Map.of("command", events)), // TODO temporary to save order
        Exchange.Exchange("changes.widgets", Map.of("*", events)),
        Exchange.Exchange(
          "goals",
          Map.of("afterpayment", new Queue("automation.goals"))
        )
      )
    );
  }
}
