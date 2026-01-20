package com.batko.cinematicketbooking.domain.impl;

import com.batko.cinematicketbooking.domain.BaseEntity;
import com.batko.cinematicketbooking.domain.enums.ValidationError;
import com.batko.cinematicketbooking.domain.exception.EntityValidationException;
import java.time.LocalDateTime;
import java.util.UUID;

public class Session extends BaseEntity {

  private static final String HALL_ID = "hallId";
  private static final String MOVIE_ID = "movieId";
  private static final String MANAGER_ID = "managerId";
  private static final String PRICE = "price";
  private static final String START_TIME = "startTime";

  private UUID hallId;
  private UUID movieId;
  private UUID managerId;
  private int price;
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  private Session() {
    super();
  }

  public Session(UUID hallId, UUID movieId, UUID managerId, int price, LocalDateTime startTime,
      Movie movie) {
    this();
    setHallId(hallId);
    setMovieId(movieId);
    setManagerId(managerId);
    setPrice(price);
    setStartTime(startTime, movie);

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  public UUID getHallId() {
    return hallId;
  }

  public void setHallId(UUID hallId) {
    clearError(HALL_ID);
    if (hallId == null) {
      addError(HALL_ID, ValidationError.SESSION_HALL_REQUIRED.getMessage());
    }
    this.hallId = hallId;
  }

  public UUID getMovieId() {
    return movieId;
  }

  public void setMovieId(UUID movieId) {
    clearError(MOVIE_ID);
    if (movieId == null) {
      addError(MOVIE_ID, ValidationError.SESSION_MOVIE_REQUIRED.getMessage());
    }
    this.movieId = movieId;
  }

  public UUID getManagerId() {
    return managerId;
  }

  public void setManagerId(UUID managerId) {
    clearError(MANAGER_ID);
    if (managerId == null) {
      addError(MANAGER_ID, ValidationError.SESSION_MANAGER_REQUIRED.getMessage());
    }
    this.managerId = managerId;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    clearError(PRICE);
    if (price <= 0) {
      addError(PRICE, ValidationError.SESSION_PRICE_INVALID.getMessage());
    }
    this.price = price;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime, Movie movie) {
    clearError(START_TIME);
    if (startTime == null) {
      addError(START_TIME, ValidationError.SESSION_START_TIME_REQUIRED.getMessage());
    } else if (movie != null) {
      this.startTime = startTime;
      this.endTime = startTime.plusMinutes(movie.getDurationMinutes());
    } else {
      addError(MOVIE_ID, ValidationError.SESSION_MOVIE_REQUIRED_FOR_CALCULATION.getMessage());
    }
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }
}