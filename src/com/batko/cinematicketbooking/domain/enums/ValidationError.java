package com.batko.cinematicketbooking.domain.enums;

public enum ValidationError {
  FIRST_NAME_EMPTY("First name cannot be empty!"),
  FIRST_NAME_LENGTH("First name must be between 3 and 35 characters"),
  LAST_NAME_EMPTY("Last name cannot be empty!"),
  LAST_NAME_LENGTH("Last name must be between 3 and 40 characters"),
  EMAIL_EMPTY("Email cannot be empty!"),
  EMAIL_INVALID("Email is not valid!"),
  PASSWORD_EMPTY("Password cannot be empty!"),
  AGE_INVALID("Age is not valid!"),
  ROLE_EMPTY("Role cannot be empty!"),

  HALL_NAME_EMPTY("Hall name cannot be empty!"),
  HALL_ROWS_INVALID("Rows must be greater than 0 and less than 24!"),
  HALL_SEATS_PER_ROW_INVALID("Seats per row must be greater than 0 and less than 32!"),

  MOVIE_TITLE_EMPTY("Movie title cannot be empty!"),
  MOVIE_DESCRIPTION_EMPTY("Movie description cannot be empty!"),
  MOVIE_DURATION_INVALID("Duration must be greater than 0 and less than 1440 minutes!"),
  MOVIE_MANAGER_REQUIRED("Manager ID is required!"),

  SEAT_HALL_REQUIRED("Hall ID is required!"),
  SEAT_TYPE_REQUIRED("Seat type is required!"),
  SEAT_ROW_INVALID("Rows must be greater than 0 and less than 24!"),
  SEAT_NUMBER_INVALID("Seats per row must be greater than 0 and less than 32!"),
  SEAT_OUT_OF_BOUNDS("Seat is outside of hall capacity!"),
  SEAT_HALL_MISMATCH("Provided Hall ID does not match the Hall object!"),

  SESSION_HALL_REQUIRED("Hall ID is required!"),
  SESSION_MOVIE_REQUIRED("Movie ID is required!"),
  SESSION_MOVIE_REQUIRED_FOR_CALCULATION("Movie object required to calculate end time"),
  SESSION_MANAGER_REQUIRED("Manager ID is required!"),
  SESSION_PRICE_INVALID("Price must be greater than 0!"),
  SESSION_START_TIME_REQUIRED("Start time is required!"),

  TICKET_USER_REQUIRED("User ID is required!"),
  TICKET_SESSION_REQUIRED("Session ID is required!"),
  TICKET_SEAT_REQUIRED("Seat ID is required!"),

  GENRE_NAME_EMPTY("Genre name cannot be empty!");

  private final String message;

  ValidationError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}