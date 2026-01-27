package com.batko.cinematicketbooking.service.dto.user;

import com.batko.cinematicketbooking.domain.enums.ValidationError;

public record UserLoginDto(String email, String password) {

  public UserLoginDto {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException(ValidationError.EMAIL_EMPTY.getMessage());
    }

    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException(ValidationError.PASSWORD_EMPTY.getMessage());
    }
  }
}
