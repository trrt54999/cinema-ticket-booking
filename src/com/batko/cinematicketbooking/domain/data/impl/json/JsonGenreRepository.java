package com.batko.cinematicketbooking.domain.data.impl.json;

import com.batko.cinematicketbooking.domain.data.repository.GenreRepository;
import com.batko.cinematicketbooking.domain.model.Genre;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Optional;

public class JsonGenreRepository extends CachedJsonRepository<Genre> implements GenreRepository {

  public JsonGenreRepository(String filename) {
    super(
        filename,
        new TypeToken<ArrayList<Genre>>() {
        }.getType()
    );
  }

  @Override
  public Optional<Genre> findByName(String name) {
    return findFirstBy(genre -> genre.getName().equalsIgnoreCase(name));
  }

  @Override
  public boolean existsByName(String name) {
    return findByName(name).isPresent();
  }
}