package com.batko.cinematicketbooking.service.dto.hall;

import com.batko.cinematicketbooking.domain.enums.ValidationError;

public record HallStoreDto(String name, int rows, int seatsPerRow) {

  public HallStoreDto {
    if (name == null || name.trim().isBlank()) {
      throw new IllegalArgumentException(ValidationError.HALL_NAME_EMPTY.getMessage());
    }
    if (rows <= 0 || rows > 24) {
      throw new IllegalArgumentException(ValidationError.HALL_ROWS_INVALID.getMessage());
    }
    if (seatsPerRow <= 0 || seatsPerRow > 32) {
      throw new IllegalArgumentException(ValidationError.HALL_SEATS_PER_ROW_INVALID.getMessage());
    }
  }
}