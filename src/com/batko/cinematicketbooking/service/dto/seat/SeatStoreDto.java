package com.batko.cinematicketbooking.service.dto.seat;

import com.batko.cinematicketbooking.domain.enums.SeatType;
import java.util.UUID;

public record SeatStoreDto(UUID hallId, int row, int number, SeatType seatType) {

  // todo у нас ще >= також мають бути обмеження
  public SeatStoreDto {
    if (hallId == null) {
      throw new IllegalArgumentException("Hall ID is required");
    }
    if (seatType == null) {
      throw new IllegalArgumentException("Seat Type is required");
    }
    if (row <= 0) {
      throw new IllegalArgumentException("Row must be positive");
    }
    if (number <= 0) {
      throw new IllegalArgumentException("Seat number must be positive");
    }
  }
}