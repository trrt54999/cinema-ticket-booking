package com.batko.cinematicketbooking.service.dto.ticket;

import java.util.UUID;

public record TicketStoreDto(UUID userId, UUID sessionId, UUID seatId) {

  public TicketStoreDto {
    if (userId == null) {
      throw new IllegalArgumentException("User ID is required");
    }
    if (sessionId == null) {
      throw new IllegalArgumentException("Session ID is required");
    }
    if (seatId == null) {
      throw new IllegalArgumentException("Seat ID is required");
    }
  }
}
