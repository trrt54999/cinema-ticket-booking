package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.Ticket;
import com.batko.cinematicketbooking.service.dto.ticket.TicketStoreDto;
import java.util.List;
import java.util.UUID;

public interface TicketService {

  Ticket create(TicketStoreDto dto);

  Ticket cancel(UUID ticketId);

  List<Ticket> getTicketsByUser(UUID userId);

  List<Ticket> getTicketsBySession(UUID sessionId);
}