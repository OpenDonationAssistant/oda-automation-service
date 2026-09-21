package io.github.opendonationassistant.automation.domain.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.opendonationassistant.automation.EphemeralVariable;
import io.github.opendonationassistant.automation.IVariable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class VariableResolverTest {

  private final Map<String, IVariable<?>> variables = Map.of(
    "nickname",
    new EphemeralVariable<>("nickname", "streamer"),
    "amount",
    new EphemeralVariable<>("amount", 42)
  );
  private final Function<String, Optional<IVariable<?>>> lookup = name ->
    Optional.ofNullable(variables.get(name));

  @Test
  public void testResolvesKnownPlaceholders() {
    var resolved = VariableResolver.resolve(
      "Hi <nickname>, you sent <amount>",
      lookup
    );

    assertEquals("Hi streamer, you sent 42", resolved);
  }

  @Test
  public void testResolvesEveryOccurrence() {
    var resolved = VariableResolver.resolve("<nickname>-<nickname>", lookup);

    assertEquals("streamer-streamer", resolved);
  }

  @Test
  public void testLeavesUnknownPlaceholdersUntouched() {
    var resolved = VariableResolver.resolve("Hi <missing>", lookup);

    assertEquals("Hi <missing>", resolved);
  }

  @Test
  public void testKeepsPlainTextWithoutPlaceholders() {
    var resolved = VariableResolver.resolve("no variables here", lookup);

    assertEquals("no variables here", resolved);
  }

  @Test
  public void testDoesNotInterpretDollarOrMustacheSyntax() {
    var resolved = VariableResolver.resolve(
      "${nickname} {{nickname}}",
      lookup
    );

    assertEquals("${nickname} {{nickname}}", resolved);
  }
}
