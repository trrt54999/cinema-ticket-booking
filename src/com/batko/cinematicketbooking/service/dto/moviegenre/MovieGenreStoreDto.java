package com.batko.cinematicketbooking.service.dto.moviegenre;

import java.util.UUID;

public record MovieGenreStoreDto(UUID movieId, UUID genreId) {

  public MovieGenreStoreDto {
    if (movieId == null) {
      throw new IllegalArgumentException("Movie ID is required");
    }
    if (genreId == null) {
      throw new IllegalArgumentException("Genre ID is required");
    }
  }
}