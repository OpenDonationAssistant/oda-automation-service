package io.github.opendonationassistant.scheduledrun;

import io.github.opendonationassistant.scheduledrun.repository.ScheduledRunData;
import io.github.opendonationassistant.scheduledrun.repository.ScheduledRunDataRepository;
import java.util.Map;
import java.util.UUID;

public class ScheduledRun {

  private final ScheduledRunData data;
  private final ScheduledRunDataRepository repository;

  public ScheduledRun(ScheduledRunData data, ScheduledRunDataRepository repository) {
    this.data = data;
    this.repository = repository;
  }

  public UUID id() {
    return data.id();
  }

  public String name() {
    return data.name();
  }

  public java.time.Instant time() {
    return data.time();
  }

  public Map<String, Object> schedule() {
    return data.schedule();
  }

  public boolean done() {
    return data.done();
  }

  public void save() {
    repository.update(data);
  }

  public void delete() {
    repository.deleteById(data.id());
  }
}
