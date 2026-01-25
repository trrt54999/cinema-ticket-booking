package com.batko.cinematicketbooking.service.dto.session;

import com.batko.cinematicketbooking.domain.enums.ValidationError;
import java.time.LocalDateTime;
import java.util.UUID;

public record SessionStoreDto(UUID hallId, UUID movieId, UUID managerId, int price,
                              LocalDateTime startTime
) {

  public SessionStoreDto {
    if (hallId == null) {
      throw new IllegalArgumentException(ValidationError.SESSION_HALL_REQUIRED.getMessage());
    }
    if (movieId == null) {
      throw new IllegalArgumentException(ValidationError.SESSION_MOVIE_REQUIRED.getMessage());
    }
    if (managerId == null) {
      throw new IllegalArgumentException(ValidationError.SESSION_MANAGER_REQUIRED.getMessage());
    }
    if (price <= 0) {
      throw new IllegalArgumentException(ValidationError.SESSION_PRICE_INVALID.getMessage());
    }
    if (startTime == null) {
      throw new IllegalArgumentException(ValidationError.SESSION_START_TIME_REQUIRED.getMessage());
    }
  }
}
