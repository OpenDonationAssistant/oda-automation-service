package io.github.opendonationassistant.automation.domain.action;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.opendonationassistant.automation.AutomationAction;
import io.github.opendonationassistant.automation.domain.Iteration;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.ui.UIFacade;
import io.github.opendonationassistant.events.ui.UIFacade.Event;
import io.github.opendonationassistant.events.ui.UIFacade.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class RunAlertAction extends AutomationAction {

  public static final String ID = "run-alert";

  private static final Pattern MAJOR_AMOUNT = Pattern.compile(
    "^[+-]?\\d+(\\.\\d+)?$"
  );

  private final ODALogger log = new ODALogger(this);
  private final TimeBasedEpochGenerator uuid =
    Generators.timeBasedEpochGenerator();
  private final UIFacade ui;

  public RunAlertAction(AutomationActionData data, UIFacade ui) {
    super(data);
    this.ui = ui;
  }

  public Optional<String> getAlertId() {
    return value("alertId").filter(id -> !id.isBlank());
  }

  @Override
  public void execute(Iteration iteration) {
    var alertId = getAlertId();
    if (alertId.isEmpty()) {
      log.warn("Skipping RunAlertAction: alertId is missing", () ->
        Map.of("recipientId", iteration.recipientId())
      );
      return;
    }
    var variables = new ArrayList<Variable>();
    variables.add(variable("alertId", alertId.get(), "string"));
    copyStringVariable(iteration, "nickname", variables);
    copyStringVariable(iteration, "message", variables);
    copyStringVariable(iteration, "system", variables);
    copyStringVariable(iteration, "levelName", variables);
    copyStringVariable(iteration, "alertmedia", variables);
    copyStringVariable(iteration, "originId", variables);
    copyStringVariable(iteration, "event", variables);
    iteration
      .resolveVariable("count", data().value())
      .ifPresent(count -> variables.add(variable("count", count, "number")));
    amount(iteration).ifPresent(amount ->
      variables.add(variable("amount", amount, "string"))
    );
    variables.add(variable("force", true, "boolean"));
    log.info("Executing RunAlertAction", () ->
      Map.of("recipientId", iteration.recipientId(), "alertId", alertId.get())
    );
    ui.sendEvent(
      iteration.recipientId(),
      new Event(uuid.generate().toString(), "Alert", variables)
    );
  }

  private Optional<String> amount(Iteration iteration) {
    return iteration
      .resolveVariable("amount", data().value())
      .map(String::valueOf)
      .map(value -> value.trim().split("\\s+", 2)[0])
      .filter(value -> MAJOR_AMOUNT.matcher(value).matches());
  }

  private void copyStringVariable(
    Iteration iteration,
    String name,
    List<Variable> variables
  ) {
    iteration
      .resolveVariable(name, data().value())
      .map(String::valueOf)
      .ifPresent(value -> variables.add(variable(name, value, "string")));
  }

  private Variable variable(String name, Object value, String type) {
    return new Variable(uuid.generate().toString(), name, value, type);
  }
}
