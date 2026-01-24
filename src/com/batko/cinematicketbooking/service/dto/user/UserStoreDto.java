package com.batko.cinematicketbooking.service.dto.user;

public record UserStoreDto(String firstName, String lastName, String email, String password,
                           int age) {

  public UserStoreDto {
    if (firstName == null || firstName.isBlank()) {
      throw new IllegalArgumentException("First name cannot be empty");
    }
    if (lastName == null || lastName.isBlank()) {
      throw new IllegalArgumentException("Last name cannot be empty");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }
    if (age < 0) {
      throw new IllegalArgumentException("Age cannot be negative");
    }
  }
}
