package com.batko.cinematicketbooking.service.dto.genre;

public record GenreStoreDto(String name) {

  public GenreStoreDto {
    if (name == null || name.trim().isBlank()) {
      throw new IllegalArgumentException("Genre name cannot be empty");
    }
  }
}