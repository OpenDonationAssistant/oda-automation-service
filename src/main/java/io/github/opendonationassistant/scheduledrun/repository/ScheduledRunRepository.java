package io.github.opendonationassistant.scheduledrun.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.scheduledrun.ScheduledRun;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class ScheduledRunRepository {

  private final ScheduledRunDataRepository repository;

  @Inject
  public ScheduledRunRepository(ScheduledRunDataRepository repository) {
    this.repository = repository;
  }

  public ScheduledRun create(String name, Instant time, Map<String, Object> schedule) {
    var data = new ScheduledRunData(
      Generators.timeBasedEpochGenerator().generate(),
      name,
      time,
      schedule,
      false
    );
    repository.save(data);
    return new ScheduledRun(data, repository);
  }

  public Optional<ScheduledRun> get(UUID id) {
    return repository.findById(id).map(this::convert);
  }

  public List<ScheduledRun> list() {
    return repository.findAll().stream().map(this::convert).toList();
  }

  private ScheduledRun convert(ScheduledRunData data) {
    return new ScheduledRun(data, repository);
  }
}
