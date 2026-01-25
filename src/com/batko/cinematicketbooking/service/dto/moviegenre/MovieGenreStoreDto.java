package com.batko.cinematicketbooking.service.dto.moviegenre;

import com.batko.cinematicketbooking.domain.enums.ValidationError;
import java.util.UUID;

public record MovieGenreStoreDto(UUID movieId, UUID genreId) {

  public MovieGenreStoreDto {
    if (movieId == null) {
      throw new IllegalArgumentException(ValidationError.MOVIE_GENRE_MOVIE_REQUIRED.getMessage());
    }
    if (genreId == null) {
      throw new IllegalArgumentException(ValidationError.MOVIE_GENRE_GENRE_REQUIRED.getMessage());
    }
  }
}