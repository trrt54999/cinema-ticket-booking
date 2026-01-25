package com.batko.cinematicketbooking.service.dto.user;

import com.batko.cinematicketbooking.domain.enums.ValidationError;

public record UserUpdateDto(String firstName, String lastName, String password,
                            Integer age) {

  public UserUpdateDto {
    if (firstName != null) {
      if (firstName.isBlank()) {
        throw new IllegalArgumentException(ValidationError.FIRST_NAME_EMPTY.getMessage());
      }
      if (firstName.length() < 3 || firstName.length() > 35) {
        throw new IllegalArgumentException(ValidationError.FIRST_NAME_LENGTH.getMessage());
      }
    }
    if (lastName != null) {
      if (lastName.isBlank()) {
        throw new IllegalArgumentException(ValidationError.LAST_NAME_EMPTY.getMessage());
      }
      if (lastName.length() < 3 || lastName.length() > 40) {
        throw new IllegalArgumentException(ValidationError.LAST_NAME_LENGTH.getMessage());
      }
    }
    if (password != null) {
      if (password.isBlank()) {
        throw new IllegalArgumentException(ValidationError.PASSWORD_EMPTY.getMessage());
      }
      if (password.length() < 6) {
        throw new IllegalArgumentException(ValidationError.PASSWORD_LENGTH.getMessage());
      }
    }
    if (age != null) {
      if (age < 0 || age > 110) {
        throw new IllegalArgumentException(ValidationError.AGE_INVALID.getMessage());
      }
    }
  }
}
