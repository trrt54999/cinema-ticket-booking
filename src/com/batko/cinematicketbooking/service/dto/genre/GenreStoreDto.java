package com.batko.cinematicketbooking.service.dto.genre;

import com.batko.cinematicketbooking.domain.enums.ValidationError;

public record GenreStoreDto(String name) {

  public GenreStoreDto {
    if (name == null || name.trim().isBlank()) {
      throw new IllegalArgumentException(ValidationError.GENRE_NAME_EMPTY.getMessage());
    }
  }
}