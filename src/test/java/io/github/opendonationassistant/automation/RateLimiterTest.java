package io.github.opendonationassistant.automation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RateLimiterTest {

  private static final String RECIPIENT = "test-recipient";
  private static final int LIMIT = 100;

  private RateLimiter limiter = new RateLimiter();
  private Instant now = Instant.parse("2026-01-01T00:00:00Z");

  @Test
  public void allowsExactlyLimitRequestsWithinWindow() {
    for (int i = 0; i < LIMIT; i++) {
      assertTrue(limiter.isAllowed(RECIPIENT, now), "request " + i);
    }

    assertFalse(limiter.isAllowed(RECIPIENT, now));
  }

  @Test
  public void allowsRequestsAfterWindowSlides() {
    for (int i = 0; i < LIMIT; i++) {
      assertTrue(limiter.isAllowed(RECIPIENT, now));
    }

    assertFalse(limiter.isAllowed(RECIPIENT, now.plusSeconds(59)));

    assertTrue(limiter.isAllowed(RECIPIENT, now.plusSeconds(61)));
  }

  @Test
  public void rateLimitsPerRecipientSeparately() {
    for (int i = 0; i < LIMIT; i++) {
      assertTrue(limiter.isAllowed("recipient1", now));
    }

    assertFalse(limiter.isAllowed("recipient1", now));
    assertTrue(limiter.isAllowed("recipient2", now));
  }

  @Test
  public void removesExpiredRecordsOutsideMinuteWindow() {
    for (int i = 0; i < LIMIT; i++) {
      assertTrue(limiter.isAllowed(RECIPIENT, now.minusSeconds(120)));
    }

    assertTrue(limiter.isAllowed(RECIPIENT, now));
    for (int i = 1; i < LIMIT; i++) {
      assertTrue(limiter.isAllowed(RECIPIENT, now));
    }

    assertFalse(limiter.isAllowed(RECIPIENT, now));
  }

  @Test
  public void deniedRequestsDoNotConsumeTheWindow() {
    for (int i = 0; i < LIMIT; i++) {
      assertTrue(limiter.isAllowed(RECIPIENT, now));
    }

    for (int i = 0; i < 10; i++) {
      assertFalse(limiter.isAllowed(RECIPIENT, now.plusSeconds(30)));
    }

    assertTrue(limiter.isAllowed(RECIPIENT, now.plusSeconds(61)));
  }
}
