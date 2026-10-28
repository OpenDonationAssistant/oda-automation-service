package io.github.opendonationassistant.automation;

import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class RateLimiter {

  private final Map<String, List<Instant>> records = new ConcurrentHashMap<>();

  public boolean isAllowed(String recipientId, Instant timestamp) {
    List<Instant> list = records.computeIfAbsent(recipientId, k ->
      new ArrayList<>()
    );
    synchronized (list) {
      Instant cutoff = timestamp.minusSeconds(60);
      list.removeIf(t -> t.isBefore(cutoff));
      if (list.size() < 10) {
        list.add(timestamp);
        return true;
      }
      return false;
    }
  }
}
