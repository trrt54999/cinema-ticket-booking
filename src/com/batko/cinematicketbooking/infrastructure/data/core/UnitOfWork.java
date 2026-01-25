package com.batko.cinematicketbooking.infrastructure.data.core;

import com.batko.cinematicketbooking.domain.Entity;
import com.batko.cinematicketbooking.infrastructure.data.repository.Repository;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class UnitOfWork<T extends Entity> {

  private final Set<T> newEntities = new LinkedHashSet<>();
  private final Set<T> dirtyEntities = new LinkedHashSet<>();
  private final Set<UUID> deletedIds = new LinkedHashSet<>();
  private final Repository<T> repository;

  public UnitOfWork(Repository<T> repository) {
    this.repository = repository;
  }

  public void registerNew(T entity) {
    UUID id = entity.getId();
    deletedIds.remove(id);
    dirtyEntities.remove(entity);
    newEntities.add(entity);
  }

  public void registerDirty(T entity) {
    UUID id = entity.getId();
    if (!newEntities.contains(entity) && !deletedIds.contains(id)) {
      dirtyEntities.add(entity);
    }
  }

  public void registerDeleted(T entity) {
    UUID id = entity.getId();
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
    repository.saveChanges();

    clear();
  }

  public void rollback() {
    clear();
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

  private void clear() {
    newEntities.clear();
    dirtyEntities.clear();
    deletedIds.clear();
  }
}
