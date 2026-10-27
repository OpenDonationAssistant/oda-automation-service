package io.github.opendonationassistant.automation.domain.trigger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import io.github.opendonationassistant.automation.AutomationTrigger;
import io.github.opendonationassistant.automation.repository.AutomationTriggerData;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class TriggerFactoryTest {

  private static final Map<String, Object> VALUE = Map.of("key", "value");

  private final TriggerFactory factory = new TriggerFactory();

  @Test
  public void testCreatesFilledDonationGoalTriggerFromStoredData() {
    assertCreatedFromStoredData(
      "donationgoal-filled",
      FilledDonationGoalTrigger.class
    );
  }

  @Test
  public void testCreatesStreamStartedTriggerFromStoredData() {
    assertCreatedFromStoredData("stream-started", StreamStartedTrigger.class);
  }

  @Test
  public void testCreatesChannelRaidedTriggerFromStoredData() {
    assertCreatedFromStoredData("channel-raided", ChannelRaidedTrigger.class);
  }

  @Test
  public void testCreatesCommandTriggerFromStoredData() {
    assertCreatedFromStoredData("command", CommandTrigger.class);
  }

  @Test
  public void testCreatesDonationTriggerFromStoredData() {
    assertCreatedFromStoredData("donation", DonationTrigger.class);
  }

  @Test
  public void testCreatesVariableEqualsValueTriggerFromStoredData() {
    assertCreatedFromStoredData(
      "variable-equals-value",
      VariableEqualsValueTrigger.class
    );
  }

  @Test
  public void testFallsBackToNeverTriggerForUnknownIdFromStoredData() {
    assertCreatedFromStoredData(
      "unknown-trigger",
      AutomationTrigger.NeverTrigger.class
    );
  }

  @Test
  public void testCreatesFilledDonationGoalTriggerFromCommand() {
    assertCreatedFromCommand(
      "donationgoal-filled",
      FilledDonationGoalTrigger.class
    );
  }

  @Test
  public void testCreatesDonationTriggerFromCommand() {
    assertCreatedFromCommand("donation", DonationTrigger.class);
  }

  @Test
  public void testCreatesVariableEqualsValueTriggerFromCommand() {
    assertCreatedFromCommand(
      "variable-equals-value",
      VariableEqualsValueTrigger.class
    );
  }

  @Test
  public void testFallsBackToNeverTriggerForUnknownIdFromCommand() {
    assertCreatedFromCommand(
      "unknown-trigger",
      AutomationTrigger.NeverTrigger.class
    );
  }

  private void assertCreatedFromStoredData(
    String id,
    Class<? extends AutomationTrigger> expected
  ) {
    var trigger = factory.from(new AutomationTriggerData(id, VALUE));

    assertInstanceOf(expected, trigger);
    assertEquals(id, trigger.data().id());
    assertEquals(VALUE, trigger.data().value());
  }

  private void assertCreatedFromCommand(
    String id,
    Class<? extends AutomationTrigger> expected
  ) {
    var trigger = factory.create(id, VALUE);

    assertInstanceOf(expected, trigger);
    assertEquals(id, trigger.data().id());
    assertEquals(VALUE, trigger.data().value());
  }
}
