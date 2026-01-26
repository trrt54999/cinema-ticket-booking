package com.batko.cinematicketbooking.service.dto.movie;

import com.batko.cinematicketbooking.domain.enums.ValidationError;
import java.util.List;
import java.util.UUID;

public record MovieStoreDto(String title, String description, int durationMinutes, UUID managerId,
                            List<UUID> genreIds
) {

  public MovieStoreDto {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException(ValidationError.MOVIE_TITLE_EMPTY.getMessage());
    }
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException(ValidationError.MOVIE_DESCRIPTION_EMPTY.getMessage());
    }
    if (durationMinutes <= 0 || durationMinutes > 1440) {
      throw new IllegalArgumentException(ValidationError.MOVIE_DURATION_INVALID.getMessage());
    }
    if (managerId == null) {
      throw new IllegalArgumentException(ValidationError.MOVIE_MANAGER_REQUIRED.getMessage());
    }
    if (genreIds == null) {
      throw new IllegalArgumentException(ValidationError.MOVIE_GENRE_EMPTY.getMessage());
    }
  }
}