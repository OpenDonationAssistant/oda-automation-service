package io.github.opendonationassistant.automation.domain.action;

import io.github.opendonationassistant.automation.IVariable;
import io.github.opendonationassistant.automation.domain.Iteration;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves {@code <variable-name>} placeholders inside a template string using the
 * variables available to the current {@link Iteration} (manual/global variables plus
 * trigger-provided ephemeral variables such as {@code nickname} or {@code system}).
 * Unknown placeholders are left untouched.
 */
public final class VariableResolver {

  private static final Pattern PLACEHOLDER = Pattern.compile(
    "<([A-Za-z_][A-Za-z0-9_.-]*)>"
  );

  private VariableResolver() {}

  public static String resolve(
    String template,
    Function<String, Optional<IVariable<?>>> lookup
  ) {
    var matcher = PLACEHOLDER.matcher(template);
    var resolved = new StringBuilder();
    while (matcher.find()) {
      var replacement = lookup
        .apply(matcher.group(1))
        .map(IVariable::value)
        .map(Object::toString)
        .orElseGet(matcher::group);
      matcher.appendReplacement(
        resolved,
        Matcher.quoteReplacement(replacement)
      );
    }
    matcher.appendTail(resolved);
    return resolved.toString();
  }

  public static String resolve(String template, Iteration iteration) {
    return resolve(template, iteration::variable);
  }
}
