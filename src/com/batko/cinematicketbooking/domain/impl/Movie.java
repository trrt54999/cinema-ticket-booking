package com.batko.cinematicketbooking.domain.impl;

import com.batko.cinematicketbooking.domain.BaseEntity;
import com.batko.cinematicketbooking.domain.enums.ValidationError;
import com.batko.cinematicketbooking.domain.exception.EntityValidationException;
import java.util.UUID;

public class Movie extends BaseEntity {

  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String DURATION = "durationMinutes";
  private static final String MANAGER_ID = "managerId";

  private UUID managerId;
  private String title;
  private String description;
  private int durationMinutes;

  private Movie() {
    super();
  }

  public Movie(UUID managerId, String title, String description, int durationMinutes) {
    this();
    setTitle(title);
    setDescription(description);
    setDurationMinutes(durationMinutes);
    setManagerId(managerId);

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  public UUID getManagerId() {
    return managerId;
  }

  public void setManagerId(UUID managerId) {
    clearError(MANAGER_ID);
    if (managerId == null) {
      addError(MANAGER_ID, ValidationError.MOVIE_MANAGER_REQUIRED.getMessage());
    }
    this.managerId = managerId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    clearError(TITLE);
    if (title == null || title.trim().isBlank()) {
      addError(TITLE, ValidationError.MOVIE_TITLE_EMPTY.getMessage());
    }
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    clearError(DESCRIPTION);
    if (description == null || description.trim().isBlank()) {
      addError(DESCRIPTION, ValidationError.MOVIE_DESCRIPTION_EMPTY.getMessage());
    }
    this.description = description;
  }

  public int getDurationMinutes() {
    return durationMinutes;
  }

  public void setDurationMinutes(int durationMinutes) {
    clearError(DURATION);
    if (durationMinutes <= 0 || durationMinutes > 1440) {
      addError(DURATION, ValidationError.MOVIE_DURATION_INVALID.getMessage());
    }
    this.durationMinutes = durationMinutes;
  }
}
