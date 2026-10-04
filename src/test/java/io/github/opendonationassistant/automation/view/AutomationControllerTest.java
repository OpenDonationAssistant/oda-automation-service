package io.github.opendonationassistant.automation.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.automation.AutomationRule;
import io.github.opendonationassistant.automation.domain.action.ActionFactory;
import io.github.opendonationassistant.automation.domain.trigger.TriggerFactory;
import io.github.opendonationassistant.automation.dto.AutomationRuleDto;
import io.github.opendonationassistant.automation.repository.AutomationActionData;
import io.github.opendonationassistant.automation.repository.AutomationRuleData;
import io.github.opendonationassistant.automation.repository.AutomationRuleDataRepository;
import io.github.opendonationassistant.automation.repository.AutomationRuleRepository;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import io.github.opendonationassistant.automation.repository.AutomationVariableRepository;
import io.github.opendonationassistant.testutils.AuthenticationGenerator;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class AutomationControllerTest {

  private static final String OWNER_ID = "owner-1";
  private static final String RULE_ID = "rule-1";
  private static final String TRIGGER_ID = "command";

  private final AutomationVariableRepository variables = mock(
    AutomationVariableRepository.class
  );
  private final AutomationRuleRepository rules = mock(
    AutomationRuleRepository.class
  );
  private final AutomationRuleDataRepository dataRepository = mock(
    AutomationRuleDataRepository.class
  );
  private final TriggerFactory triggerFactory = mock(TriggerFactory.class);
  private final ActionFactory actionFactory = mock(ActionFactory.class);
  private final AutomationController controller = new AutomationController(
    variables,
    rules
  );

  @Test
  public void testListAutomationsFiltersByTrigger() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    var pageable = Pageable.from(0, 20);
    when(rules.listByRecipientIdAndTrigger(any(), any(), any())).thenReturn(
      Page.of(List.of(rule()), pageable, 1L)
    );

    HttpResponse<Page<AutomationRuleDto>> response = controller.listAutomations(
      auth,
      TRIGGER_ID,
      pageable
    );

    assertEquals(HttpStatus.OK, response.getStatus());
    Page<AutomationRuleDto> body = Optional.ofNullable(response.body()).orElseThrow();
    assertEquals(1, body.getContent().size());
    assertEquals(RULE_ID, body.getContent().get(0).id());
    verify(rules).listByRecipientIdAndTrigger(OWNER_ID, TRIGGER_ID, pageable);
    verify(rules, never()).listByRecipientId(any(), any());
  }

  @Test
  public void testListAutomationsReturnsAllWhenTriggerAbsent() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    var pageable = Pageable.from(0, 20);
    when(rules.listByRecipientId(any(), any())).thenReturn(
      Page.of(List.of(rule()), pageable, 1L)
    );

    HttpResponse<Page<AutomationRuleDto>> response = controller.listAutomations(
      auth,
      null,
      pageable
    );

    assertEquals(HttpStatus.OK, response.getStatus());
    verify(rules).listByRecipientId(OWNER_ID, pageable);
    verify(rules, never()).listByRecipientIdAndTrigger(any(), any(), any());
  }

  @Test
  public void testListAutomationsTreatsBlankTriggerAsUnfiltered() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    var pageable = Pageable.from(0, 20);
    when(rules.listByRecipientId(any(), any())).thenReturn(Page.empty());

    controller.listAutomations(auth, "   ", pageable);

    verify(rules).listByRecipientId(OWNER_ID, pageable);
  }

  @Test
  public void testListAutomationsDefaultsToFirstPageWhenUnpaged() {
    var auth = AuthenticationGenerator.forUser(OWNER_ID);
    when(rules.listByRecipientIdAndTrigger(any(), any(), any())).thenReturn(
      Page.empty()
    );

    controller.listAutomations(auth, TRIGGER_ID, Pageable.unpaged());

    verify(rules).listByRecipientIdAndTrigger(
      OWNER_ID,
      TRIGGER_ID,
      Pageable.from(0, 20)
    );
  }

  private AutomationRule rule() {
    return new AutomationRule(
      dataRepository,
      triggerFactory,
      actionFactory,
      new AutomationRuleData(
        RULE_ID,
        "Rule",
        OWNER_ID,
        List.of(new AutomationTriggerData(TRIGGER_ID, Map.of("ruleId", RULE_ID))),
        List.<AutomationActionData>of(),
        true
      )
    );
  }
}
