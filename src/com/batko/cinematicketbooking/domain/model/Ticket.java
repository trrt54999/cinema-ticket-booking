package com.batko.cinematicketbooking.domain.model;

import com.batko.cinematicketbooking.domain.BaseEntity;
import com.batko.cinematicketbooking.domain.enums.TicketStatus;
import com.batko.cinematicketbooking.domain.enums.ValidationError;
import com.batko.cinematicketbooking.domain.exception.EntityValidationException;
import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket extends BaseEntity {

  private static final String USER_ID = "userId";
  private static final String SESSION_ID = "sessionId";
  private static final String SEAT_ID = "seatId";

  private UUID userId;
  private UUID sessionId;
  private UUID seatId;
  private TicketStatus status;
  private LocalDateTime purchaseDate;

  private Ticket() {
    super();
  }

  public Ticket(UUID userId, UUID sessionId, UUID seatId) {
    this();
    setUserId(userId);
    setSessionId(sessionId);
    setSeatId(seatId);

    this.status = TicketStatus.BOOKED;
    this.purchaseDate = LocalDateTime.now();

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    clearError(USER_ID);
    if (userId == null) {
      addError(USER_ID, ValidationError.TICKET_USER_REQUIRED.getMessage());
    }
    this.userId = userId;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public void setSessionId(UUID sessionId) {
    clearError(SESSION_ID);
    if (sessionId == null) {
      addError(SESSION_ID, ValidationError.TICKET_SESSION_REQUIRED.getMessage());
    }
    this.sessionId = sessionId;
  }

  public UUID getSeatId() {
    return seatId;
  }

  public void setSeatId(UUID seatId) {
    clearError(SEAT_ID);
    if (seatId == null) {
      addError(SEAT_ID, ValidationError.TICKET_SEAT_REQUIRED.getMessage());
    }
    this.seatId = seatId;
  }

  public TicketStatus getStatus() {
    return status;
  }

  public void setStatus(TicketStatus status) {
    this.status = status;
  }

  public LocalDateTime getPurchaseDate() {
    return purchaseDate;
  }

  public void setPurchaseDate(LocalDateTime purchaseDate) {
    this.purchaseDate = purchaseDate;
  }
}