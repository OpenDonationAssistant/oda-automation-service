package io.github.opendonationassistant.automation;

public interface IVariable<T> {
  String name();
  T value();
}
