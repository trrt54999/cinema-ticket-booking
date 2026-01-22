package com.batko.cinematicketbooking.infrastructure.data.impl.json;

import com.batko.cinematicketbooking.domain.model.Ticket;
import com.batko.cinematicketbooking.infrastructure.data.repository.TicketRepository;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketJsonRepository extends CachedJsonRepository<Ticket> implements TicketRepository {

  TicketJsonRepository(String filename) {
    super(
        filename,
        new TypeToken<ArrayList<Ticket>>() {
        }.getType()
    );
  }

  @Override
  public List<Ticket> findByUserId(UUID userId) {
    return findBy(ticket -> ticket.getUserId().equals(userId));
  }

  @Override
  public List<Ticket> findBySessionId(UUID sessionId) {
    return findBy(ticket -> ticket.getSessionId().equals(sessionId));
  }

  @Override
  public boolean existsBySessionIdAndSeatId(UUID sessionId, UUID seatId) {
    return findFirstBy(ticket ->
        ticket.getSessionId().equals(sessionId) &&
            ticket.getSeatId().equals(seatId)
    ).isPresent();
  }
}