package io.github.opendonationassistant.automation.repository;

import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

@Serdeable
public record AutomationActionData(String id, Map<String, Object> value) {}
