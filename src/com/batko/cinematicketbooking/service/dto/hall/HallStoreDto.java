package com.batko.cinematicketbooking.service.dto.hall;

public record HallStoreDto(String name, int rows, int seatsPerRow) {

  // todo те саме з >=
  public HallStoreDto {
    if (name == null || name.trim().isBlank()) {
      throw new IllegalArgumentException("Hall name is required");
    }
    if (rows <= 0) {
      throw new IllegalArgumentException("Rows must be positive");
    }
    if (seatsPerRow <= 0) {
      throw new IllegalArgumentException("Seats per row must be positive");
    }
  }
}