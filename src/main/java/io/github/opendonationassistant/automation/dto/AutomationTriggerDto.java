package io.github.opendonationassistant.automation.dto;

import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

@Serdeable
public record AutomationTriggerDto(String id, Map<String, Object> value) {}
