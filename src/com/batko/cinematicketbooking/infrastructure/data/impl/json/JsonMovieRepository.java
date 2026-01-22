package com.batko.cinematicketbooking.infrastructure.data.impl.json;

import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieRepository;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JsonMovieRepository extends CachedJsonRepository<Movie> implements MovieRepository {

  JsonMovieRepository(String filename) {
    super(
        filename,
        new TypeToken<ArrayList<Movie>>() {
        }.getType()
    );
  }

  @Override
  public List<Movie> findByTitle(String title) {
    return findBy(movie -> movie.getTitle().equalsIgnoreCase(title));
  }

  @Override
  public boolean existsByTitle(String title) {
    return !findByTitle(title).isEmpty();
  }

  @Override
  public List<Movie> findByManagerId(UUID managerId) {
    return findBy(movie -> movie.getManagerId().equals(managerId));
  }
}