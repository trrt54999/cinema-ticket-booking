package com.batko.cinematicketbooking.service.dto.movie;

import java.util.List;
import java.util.UUID;

public record MovieStoreDto(String title, String description, int durationMinutes, UUID managerId,
                            List<UUID> genreIds
) {

  public MovieStoreDto {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Title is required");
    }
    if (managerId == null) {
      throw new IllegalArgumentException("Manager ID is required");
    }
    if (durationMinutes <= 0) {
      throw new IllegalArgumentException("Duration must be positive");
    }
    if (genreIds == null) {
      genreIds = List.of();
    }
  }
}