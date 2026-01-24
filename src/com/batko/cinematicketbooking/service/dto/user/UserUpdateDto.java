package com.batko.cinematicketbooking.service.dto.user;

public record UserUpdateDto(String firstName, String lastName, String password,
                            int age) {

  public UserUpdateDto {
    if (age < 0) {
      throw new IllegalArgumentException("Age cannot be negative");
    }
  }
}
