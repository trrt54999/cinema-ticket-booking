package com.batko.cinematicketbooking.service.dto.user;

import com.batko.cinematicketbooking.domain.enums.ValidationError;

public record UserVerificationDto(String email, String verificationCode) {

  public UserVerificationDto {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException(ValidationError.EMAIL_EMPTY.getMessage());
    }
    if (verificationCode == null || verificationCode.isBlank()) {
      throw new IllegalArgumentException(ValidationError.VALIDATION_CODE_EMPTY.getMessage());
    }
  }
}
