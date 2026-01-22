package com.batko.cinematicketbooking.infrastructure.data.impl.json;

import com.batko.cinematicketbooking.domain.Entity;
import com.batko.cinematicketbooking.infrastructure.data.adapter.LocalDateTimeAdapter;
import com.batko.cinematicketbooking.infrastructure.data.core.IdentityMap;
import com.batko.cinematicketbooking.infrastructure.data.exception.StorageException;
import com.batko.cinematicketbooking.infrastructure.data.repository.Repository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class CachedJsonRepository<T extends Entity> implements Repository<T> {

  protected final Path filePath;
  protected final Gson gson;
  protected final Type listType;
  protected final IdentityMap<T> identityMap = new IdentityMap<>();

  private boolean cacheValid = false;
  private List<T> cachedList = null;
  
  protected CachedJsonRepository(String filename, Type listType) {
    this.filePath = Path.of(filename);
    this.listType = listType;
    this.gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .serializeNulls()
        .create();
    ensureDirectoryExists();
  }

  private void ensureDirectoryExists() {
    Path parent = filePath.getParent();
    if (parent != null && !Files.exists(parent)) {
      try {
        Files.createDirectories(parent);
      } catch (IOException e) {
        throw new StorageException("Cannot create directory: " + parent, e);
      }
    }
  }

  @Override
  public T save(T entity) {
    UUID id = entity.getId();
    identityMap.put(id, entity);
    invalidateCache();

    List<T> entities = loadFromFile();

    boolean found = false;
    for (int i = 0; i < entities.size(); i++) {
      if (entities.get(i).getId().equals(id)) {
        entities.set(i, entity);
        found = true;
        break;
      }
    }

    if (!found) {
      entities.add(entity);
    }

    writeToFile(entities);
    return entity;
  }

  @Override
  public Optional<T> findById(UUID id) {
    Optional<T> cached = identityMap.get(id);
    if (cached.isPresent()) {
      return cached;
    }

    Optional<T> found = findAllInternal().stream()
        .filter(entity -> entity.getId().equals(id))
        .findFirst();

    found.ifPresent(entity -> identityMap.put(id, entity));

    return found;
  }

  @Override
  public List<T> findAll() {
    return new ArrayList<>(findAllInternal());
  }

  @Override
  public boolean deleteById(UUID id) {
    identityMap.remove(id);
    invalidateCache();

    List<T> entities = loadFromFile();
    boolean removed = entities.removeIf(entity -> entity.getId().equals(id));

    if (removed) {
      writeToFile(entities);
    }
    return removed;
  }

  @Override
  public boolean delete(T entity) {
    return deleteById(entity.getId());
  }

  @Override
  public boolean existsById(UUID id) {
    return identityMap.contains(id) || findById(id).isPresent();
  }

  @Override
  public long count() {
    return findAllInternal().size();
  }

  protected void invalidateCache() {
    cacheValid = false;
    cachedList = null;
  }

  public void clearCache() {
    identityMap.clear();
    invalidateCache();
  }

  protected List<T> findBy(Predicate<T> predicate) {
    return findAllInternal().stream()
        .filter(predicate)
        .toList();
  }

  protected Optional<T> findFirstBy(Predicate<T> predicate) {
    return findAllInternal().stream()
        .filter(predicate)
        .findFirst();
  }

  protected List<T> findAllInternal() {
    if (cacheValid && cachedList != null) {
      return cachedList;
    }

    cachedList = loadFromFile();
    cacheValid = true;

    for (T entity : cachedList) {
      UUID id = entity.getId();
      if (!identityMap.contains(id)) {
        identityMap.put(id, entity);
      }
    }

    return cachedList;
  }

  private List<T> loadFromFile() {
    if (!Files.exists(filePath)) {
      return new ArrayList<>();
    }

    try (Reader reader = new FileReader(filePath.toFile())) {
      List<T> entities = gson.fromJson(reader, listType);
      return entities != null ? new ArrayList<>(entities) : new ArrayList<>();
    } catch (IOException e) {
      throw new StorageException("Error with reading: " + filePath, e);
    }
  }

  protected void writeToFile(List<T> entities) {
    try (Writer writer = new FileWriter(filePath.toFile())) {
      gson.toJson(entities, writer);
    } catch (IOException e) {
      throw new StorageException("Error with writing in file: " + filePath, e);
    }
  }
}