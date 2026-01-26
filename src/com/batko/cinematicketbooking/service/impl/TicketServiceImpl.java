package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.enums.TicketStatus;
import com.batko.cinematicketbooking.domain.model.Ticket;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.TicketRepository;
import com.batko.cinematicketbooking.service.contract.TicketService;
import com.batko.cinematicketbooking.service.dto.ticket.TicketStoreDto;
import java.util.List;
import java.util.UUID;

public class TicketServiceImpl implements TicketService {

  private final TicketRepository ticketRepo;
  private final UnitOfWork<Ticket> ticketUoW;

  public TicketServiceImpl(TicketRepository ticketRepo, UnitOfWork<Ticket> ticketUoW) {
    this.ticketRepo = ticketRepo;
    this.ticketUoW = ticketUoW;
  }

  @Override
  public Ticket create(TicketStoreDto dto) {
    if (ticketRepo.existsBySessionIdAndSeatId(dto.sessionId(), dto.seatId())) {
      throw new IllegalArgumentException("This seat is already booked");
    }
    Ticket ticket = new Ticket(dto.userId(), dto.sessionId(), dto.seatId());

    ticketUoW.registerNew(ticket);
    ticketUoW.commit();

    return ticket;
  }

  @Override
  public Ticket cancel(UUID ticketId) {
    Ticket ticket = ticketRepo.findById(ticketId)
        .orElseThrow(() -> new IllegalArgumentException("Tickets not found"));

    ticket.setStatus(TicketStatus.CANCELED);

    ticketUoW.registerDirty(ticket);
    ticketUoW.commit();

    return ticket;
  }

  @Override
  public List<Ticket> getTicketsByUser(UUID userId) {
    return ticketRepo.findByUserId(userId);
  }

  @Override
  public List<Ticket> getTicketsBySession(UUID sessionId) {
    List<Ticket> tickets = ticketRepo.findBySessionId(sessionId);

    return tickets.stream()
        .filter(t -> t.getStatus() != TicketStatus.CANCELED)
        .toList();
  }
}