package com.batko.cinematicketbooking.service.dto.session;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionStoreDto(UUID hallId, UUID movieId, UUID managerId, int price,
                              LocalDateTime startTime
) {

  public SessionStoreDto {
    if (hallId == null) {
      throw new IllegalArgumentException("Hall ID is required");
    }
    if (movieId == null) {
      throw new IllegalArgumentException("Movie ID is required");
    }
    if (managerId == null) {
      throw new IllegalArgumentException("Manager ID is required");
    }
    if (startTime == null) {
      throw new IllegalArgumentException("Start time is required");
    }
    if (price <= 0) {
      throw new IllegalArgumentException("Price must be positive");
    }
  }
}
