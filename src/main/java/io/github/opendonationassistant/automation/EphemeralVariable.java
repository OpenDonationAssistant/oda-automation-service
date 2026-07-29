package io.github.opendonationassistant.automation;

public class EphemeralVariable<T> implements IVariable<T> {

  private String name;
  private T value;

  public EphemeralVariable(String name, T value) {
    this.name = name;
    this.value = value;
  }

  public String name() {
    return this.name;
  }

  public T value() {
    return value;
  }
}
