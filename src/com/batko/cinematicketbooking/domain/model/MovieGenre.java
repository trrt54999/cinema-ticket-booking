package com.batko.cinematicketbooking.domain.model;

import com.batko.cinematicketbooking.domain.BaseEntity;
import com.batko.cinematicketbooking.domain.exception.EntityValidationException;
import java.util.Objects;
import java.util.UUID;

public class MovieGenre extends BaseEntity {

  private final UUID movieId;
  private final UUID genreId;

  public MovieGenre(UUID movieId, UUID genreId) {
    super();
    if (movieId == null) {
      addError("movieId", "Movie ID required");
    }
    if (genreId == null) {
      addError("genreId", "Genre ID required");
    }
    this.movieId = movieId;
    this.genreId = genreId;

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MovieGenre that = (MovieGenre) o;
    return Objects.equals(movieId, that.movieId) && Objects.equals(genreId, that.genreId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(movieId, genreId);
  }
}