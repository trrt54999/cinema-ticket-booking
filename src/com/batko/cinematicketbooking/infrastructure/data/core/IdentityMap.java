package com.batko.cinematicketbooking.infrastructure.data.core;

import com.batko.cinematicketbooking.domain.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class IdentityMap<T extends Entity> {

  private final Map<UUID, T> cache = new HashMap<>();

  public Optional<T> get(UUID id) {
    return Optional.ofNullable(cache.get(id));
  }

  public void put(UUID id, T entity) {
    cache.put(id, entity);
  }

  public void remove(UUID id) {
    cache.remove(id);
  }

  public void clear() {
    cache.clear();
  }

  public boolean contains(UUID id) {
    return cache.containsKey(id);
  }

  public int size() {
    return cache.size();
  }
}