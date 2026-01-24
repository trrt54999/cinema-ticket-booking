package com.batko.cinematicketbooking.service.util;

import java.security.SecureRandom;

public final class CodeGenerator {

  private static final SecureRandom RANDOM = new SecureRandom();
  private static final String ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
  private static final int CODE_LENGTH = 6;

  private CodeGenerator() {
  }

  public static String generateVerificationCode() {
    StringBuilder sb = new StringBuilder(CODE_LENGTH);
    for (int i = 0; i < CODE_LENGTH; i++) {
      int index = RANDOM.nextInt(ALPHABET.length());
      sb.append(ALPHABET.charAt(index));
    }
    return sb.toString();
  }
}