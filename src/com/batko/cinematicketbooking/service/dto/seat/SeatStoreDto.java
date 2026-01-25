package com.batko.cinematicketbooking.service.dto.seat;

import com.batko.cinematicketbooking.domain.enums.SeatType;
import com.batko.cinematicketbooking.domain.enums.ValidationError;
import java.util.UUID;

public record SeatStoreDto(UUID hallId, int row, int number, SeatType seatType) {

  public SeatStoreDto {
    if (hallId == null) {
      throw new IllegalArgumentException(ValidationError.SEAT_HALL_REQUIRED.getMessage());
    }
    if (row <= 0) {
      throw new IllegalArgumentException(ValidationError.SEAT_ROW_INVALID.getMessage());
    }
    if (number <= 0) {
      throw new IllegalArgumentException(ValidationError.SEAT_NUMBER_INVALID.getMessage());
    }
    if (seatType == null) {
      throw new IllegalArgumentException(ValidationError.SEAT_TYPE_REQUIRED.getMessage());
    }
  }
}