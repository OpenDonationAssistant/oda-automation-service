package io.github.opendonationassistant;

import io.github.opendonationassistant.rabbit.AMQPConfiguration;
import io.github.opendonationassistant.rabbit.Exchange;
import io.github.opendonationassistant.rabbit.Queue;
import io.github.opendonationassistant.rabbit.RabbitClient;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.ApplicationContextConfigurer;
import io.micronaut.context.annotation.ContextConfigurer;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.rabbitmq.connect.ChannelInitializer;
import io.micronaut.rabbitmq.connect.ChannelPool;
import io.micronaut.runtime.Micronaut;
import io.micronaut.serde.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@OpenAPIDefinition(info = @Info(title = "ODA Automation Service"))
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
    var bindings = new ArrayList<Exchange>();
    bindings.addAll(EventsListener.BINDING);
    bindings.addAll(ProcessingListener.BINDING);
    bindings.addAll(
      List.of(
        Exchange.Exchange(
          "automation",
          Map.of("command", EventsListener.QUEUE)
        ), // TODO temporary to save order
        Exchange.Exchange(
          "commands",
          Map.of("command.RepeatAlertCommand", EventsListener.QUEUE)
        ),
        Exchange.Exchange("changes.widgets", Map.of("*", EventsListener.QUEUE)),
        Exchange.Exchange(
          "goals",
          Map.of("afterpayment", new Queue("automation.goals"))
        )
      )
    );
    return new AMQPConfiguration(bindings);
  }

  @Singleton
  @Named("commands")
  public RabbitClient commandsFacade(ChannelPool pool, ObjectMapper mapper) {
    return new RabbitClient(pool, mapper, "commands");
  }
}
