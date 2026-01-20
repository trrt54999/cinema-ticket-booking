package com.batko.cinematicketbooking.domain.enums;

public enum UserRole {
  USER("user"),
  MANAGER("manager");

  private final String name;

  UserRole(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
