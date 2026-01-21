package com.batko.cinematicketbooking.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseEntity implements Entity {

  private final UUID id;

  protected Map<String, List<String>> errors;
  // todo спитати за це, бо protected transient Map<String, List<String>> errors;
  // (щоб в json не ліз)

  protected BaseEntity() {
    this.id = UUID.randomUUID();
    this.errors = new HashMap<>();
  }

  protected void addError(String field, String message) {
    this.errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
  }

  protected void clearError(String field) {
    this.errors.remove(field);
  }

  public Map<String, List<String>> getErrors() {
    return new HashMap<>(errors);
  }

  public boolean isValid() {
    return errors.isEmpty();
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BaseEntity that = (BaseEntity) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
