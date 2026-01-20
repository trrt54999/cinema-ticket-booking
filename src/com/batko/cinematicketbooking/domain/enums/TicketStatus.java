package com.batko.cinematicketbooking.domain.enums;

public enum TicketStatus {

  BOOKED("booked"),
  CANCELED("canceled");

  private final String statusName;

  TicketStatus(String statusName) {
    this.statusName = statusName;
  }

  public String getStatusName() {
    return statusName;
  }
}
