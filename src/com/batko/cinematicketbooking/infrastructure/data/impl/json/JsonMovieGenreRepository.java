package com.batko.cinematicketbooking.infrastructure.data.impl.json;

import com.batko.cinematicketbooking.domain.model.MovieGenre;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieGenreRepository;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JsonMovieGenreRepository extends CachedJsonRepository<MovieGenre> implements
    MovieGenreRepository {

  JsonMovieGenreRepository(String filename) {
    super(
        filename,
        new TypeToken<ArrayList<MovieGenre>>() {
        }.getType()
    );
  }

  @Override
  public List<MovieGenre> findByMovieId(UUID movieId) {
    return findBy(movieGenre -> movieGenre.getMovieId().equals(movieId));
  }

  @Override
  public List<MovieGenre> findByGenreId(UUID genreId) {
    return findBy(movieGenre -> movieGenre.getGenreId().equals(genreId));
  }
}