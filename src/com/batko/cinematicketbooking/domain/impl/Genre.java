package com.batko.cinematicketbooking.domain.impl;

import com.batko.cinematicketbooking.domain.BaseEntity;
import com.batko.cinematicketbooking.domain.enums.ValidationError;
import com.batko.cinematicketbooking.domain.exception.EntityValidationException;

public class Genre extends BaseEntity {

  private static final String NAME = "name";

  private String name;

  private Genre() {
    super();
  }

  public Genre(String name) {
    this();
    setName(name);

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    clearError(NAME);
    if (name == null || name.trim().isBlank()) {
      addError(NAME, ValidationError.GENRE_NAME_EMPTY.getMessage());
    }
    this.name = name;
  }
}