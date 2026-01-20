package com.batko.cinematicketbooking.domain.impl;

import com.batko.cinematicketbooking.domain.BaseEntity;
import com.batko.cinematicketbooking.domain.enums.ValidationError;
import com.batko.cinematicketbooking.domain.exception.EntityValidationException;
import java.util.UUID;

public class Seat extends BaseEntity {

  private static final String HALL_ID = "hallId";
  private static final String NUMBER = "number";
  private static final String ROW = "row";

  private UUID hallId;
  private int number;
  private int row;

  private Seat() {
    super();
  }

  public Seat(UUID hallId, int row, int number, Hall hall) {
    this();
    setHallId(hallId);
    validateHallMatch(hallId, hall);
    validateSeatCoordinates(row, number, hall);

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  private void validateHallMatch(UUID hallId, Hall hall) {
    if (hall == null || !hall.getId().equals(hallId)) {
      addError(HALL_ID, ValidationError.SEAT_HALL_MISMATCH.getMessage());
    }
  }

  private void validateSeatCoordinates(int row, int number, Hall hall) {
    clearError(ROW);
    clearError(NUMBER);

    if (row <= 0) {
      addError(ROW, ValidationError.SEAT_OUT_OF_BOUNDS.getMessage() + hall.getRows() + ")");
    } else if (hall != null && row > hall.getRows()) {
      addError(ROW,
          ValidationError.SEAT_OUT_OF_BOUNDS.getMessage() + " (Max rows: " + hall.getRows() + ")");
    }
    this.row = row;

    if (number <= 0) {
      addError(NUMBER, ValidationError.SEAT_NUMBER_INVALID.getMessage());
    } else if (hall != null && number > hall.getSeatsPerRow()) {
      addError(NUMBER,
          ValidationError.SEAT_OUT_OF_BOUNDS.getMessage() + " (Max seats: " + hall.getSeatsPerRow()
              + ")");
    }
    this.number = number;
  }

  public UUID getHallId() {
    return hallId;
  }

  private void setHallId(UUID hallId) {
    clearError(HALL_ID);
    if (hallId == null) {
      addError(HALL_ID, ValidationError.SEAT_HALL_REQUIRED.getMessage());
    }
    this.hallId = hallId;
  }

  public int getNumber() {
    return number;
  }

  public int getRow() {
    return row;
  }
}