package io.github.opendonationassistant;

import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.ApplicationContextConfigurer;
import io.micronaut.context.annotation.ContextConfigurer;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
  info = @Info(
    title = "ODA Automation Service",
    version = "0.1",
    description = "ODA Automation Service",
    license = @License(
      name = "GPL-3.0",
      url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
    ),
    contact = @Contact(name = "stCarolas", email = "stcarolas@gmail.com")
  )
)
public class Application {

  @ContextConfigurer
  public static class Configurer implements ApplicationContextConfigurer {

    @Override
    public void configure(@NonNull ApplicationContextBuilder builder) {
      builder.defaultEnvironments("standalone");
    }
  }

  public static void main(String[] args) {
    Micronaut.run(Application.class, args);
  }
}
