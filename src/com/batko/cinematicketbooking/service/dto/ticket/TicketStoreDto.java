package com.batko.cinematicketbooking.service.dto.ticket;

import com.batko.cinematicketbooking.domain.enums.ValidationError;
import java.util.UUID;

public record TicketStoreDto(UUID userId, UUID sessionId, UUID seatId) {

  public TicketStoreDto {
    if (userId == null) {
      throw new IllegalArgumentException(ValidationError.TICKET_USER_REQUIRED.getMessage());
    }
    if (sessionId == null) {
      throw new IllegalArgumentException(ValidationError.TICKET_SESSION_REQUIRED.getMessage());
    }
    if (seatId == null) {
      throw new IllegalArgumentException(ValidationError.TICKET_SEAT_REQUIRED.getMessage());
    }
  }
}
