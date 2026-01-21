package com.batko.cinematicketbooking.domain.model;

import com.batko.cinematicketbooking.domain.BaseEntity;
import com.batko.cinematicketbooking.domain.enums.ValidationError;
import com.batko.cinematicketbooking.domain.exception.EntityValidationException;

public class Hall extends BaseEntity {

  private static final String NAME = "name";
  private static final String ROWS = "rows";
  private static final String SEATS_PER_ROW = "seatsPerRow";

  private String name;
  private int rows;
  private int seatsPerRow;

  private Hall() {
    super();
  }

  public Hall(String name, int rows, int seatsPerRow) {
    this();
    setName(name);
    setRows(rows);
    setSeatsPerRow(seatsPerRow);

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
      addError(NAME, ValidationError.HALL_NAME_EMPTY.getMessage());
    }
    this.name = name;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    clearError(ROWS);
    if (rows <= 0 || rows > 24) {
      addError(ROWS, ValidationError.HALL_ROWS_INVALID.getMessage());
    }
    this.rows = rows;
  }

  public int getSeatsPerRow() {
    return seatsPerRow;
  }

  public void setSeatsPerRow(int seatsPerRow) {
    clearError(SEATS_PER_ROW);
    if (seatsPerRow <= 0 || seatsPerRow > 32) {
      addError(SEATS_PER_ROW, ValidationError.HALL_SEATS_PER_ROW_INVALID.getMessage());
    }
    this.seatsPerRow = seatsPerRow;
  }
}