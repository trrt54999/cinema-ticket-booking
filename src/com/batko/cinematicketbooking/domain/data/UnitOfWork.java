package com.batko.cinematicketbooking.domain.data;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class UnitOfWork<T> {

  private final Set<T> newEntities = new LinkedHashSet<>();
  private final Set<T> dirtyEntities = new LinkedHashSet<>();
  private final Set<UUID> deletedIds = new LinkedHashSet<>();
  private final Function<T, UUID> idExtractor;
  private final Repository<T> repository;

  public UnitOfWork(Repository<T> repository, Function<T, UUID> idExtractor) {
    this.repository = repository;
    this.idExtractor = idExtractor;
  }

  public void registerNew(T entity) {
    UUID id = idExtractor.apply(entity);
    deletedIds.remove(id);
    dirtyEntities.remove(entity);
    newEntities.add(entity);
  }

  public void registerDirty(T entity) {
    UUID id = idExtractor.apply(entity);
    if (!newEntities.contains(entity) && !deletedIds.contains(id)) {
      dirtyEntities.add(entity);
    }
  }

  public void registerDeleted(T entity) {
    UUID id = idExtractor.apply(entity);
    if (newEntities.remove(entity)) {
      return;
    }
    dirtyEntities.remove(entity);
    deletedIds.add(id);
  }

  public void commit() {
    for (T entity : newEntities) {
      repository.save(entity);
    }

    for (T entity : dirtyEntities) {
      repository.save(entity);
    }

    for (UUID id : deletedIds) {
      repository.deleteById(id);
    }

    clear();
  }

  public void rollback() {
    clear();
  }

  public void clear() {
    newEntities.clear();
    dirtyEntities.clear();
    deletedIds.clear();
  }

  public boolean hasChanges() {
    return !newEntities.isEmpty() || !dirtyEntities.isEmpty() || !deletedIds.isEmpty();
  }

  public String getChangesSummary() {
    return String.format(
        "New: %d, Dirty: %d, Deleted: %d",
        newEntities.size(),
        dirtyEntities.size(),
        deletedIds.size()
    );
  }
}
