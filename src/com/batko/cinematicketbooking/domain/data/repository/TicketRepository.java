package com.batko.cinematicketbooking.domain.data.repository;

import com.batko.cinematicketbooking.domain.model.Ticket;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends Repository<Ticket> {

  List<Ticket> findByUserId(UUID userId);

  List<Ticket> findBySessionId(UUID sessionId);

  boolean existsBySessionIdAndSeatId(UUID sessionId, UUID seatId);
}