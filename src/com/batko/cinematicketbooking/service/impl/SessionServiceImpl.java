package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.enums.TicketStatus;
import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.domain.model.Ticket;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.SessionRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.TicketRepository;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.dto.session.SessionStoreDto;
import com.batko.cinematicketbooking.service.dto.session.SessionUpdateDto;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SessionServiceImpl implements SessionService {

  private static final String MOVIE_NOT_FOUND = "Movie not found";

  private final SessionRepository sessionRepo;
  private final MovieRepository movieRepo;
  private final TicketRepository ticketRepo;
  private final UnitOfWork<Session> sessionUoW;
  private final UnitOfWork<Ticket> ticketUoW;

  public SessionServiceImpl(SessionRepository sessionRepo, MovieRepository movieRepo,
      TicketRepository ticketRepo, UnitOfWork<Session> sessionUoW, UnitOfWork<Ticket> ticketUoW) {
    this.sessionRepo = sessionRepo;
    this.movieRepo = movieRepo;
    this.ticketRepo = ticketRepo;
    this.sessionUoW = sessionUoW;
    this.ticketUoW = ticketUoW;
  }

  @Override
  public Session create(SessionStoreDto dto) {
    Movie movie = movieRepo.findById(dto.movieId())
        .orElseThrow(() -> new IllegalArgumentException(MOVIE_NOT_FOUND));

    LocalDateTime newStart = dto.startTime();
    LocalDateTime newEnd = newStart.plusMinutes(movie.getDurationMinutes());

    validateSessionOverlap(dto.hallId(), newStart, newEnd, null);

    Session session = new Session(dto.hallId(), dto.movieId(), dto.managerId(), dto.price(),
        dto.startTime(), movie);

    sessionUoW.registerNew(session);
    sessionUoW.commit();

    return session;
  }

  @Override
  public Session update(UUID sessionId, SessionUpdateDto dto) {
    Session session = getById(sessionId);
    boolean isDirty = false;

    UUID targetHallId = (dto.hallId() != null) ? dto.hallId() : session.getHallId();
    UUID targetMovieId = (dto.movieId() != null) ? dto.movieId() : session.getMovieId();
    LocalDateTime targetStartTime =
        (dto.startTime() != null) ? dto.startTime() : session.getStartTime();

    boolean needValidation =
        dto.hallId() != null || dto.movieId() != null || dto.startTime() != null;

    if (needValidation) {
      Movie movie = movieRepo.findById(targetMovieId)
          .orElseThrow(() -> new IllegalArgumentException(MOVIE_NOT_FOUND));
      LocalDateTime targetEndTime = targetStartTime.plusMinutes(movie.getDurationMinutes());

      validateSessionOverlap(targetHallId, targetStartTime, targetEndTime, sessionId);
    }

    if (dto.hallId() != null && !dto.hallId().equals(session.getHallId())) {
      session.setHallId(dto.hallId());
      isDirty = true;
    }
    if (dto.price() != null && !dto.price().equals(session.getPrice())) {
      session.setPrice(dto.price());
      isDirty = true;
    }

    if (dto.movieId() != null || dto.startTime() != null) {
      Movie movie = movieRepo.findById(targetMovieId)
          .orElseThrow(() -> new IllegalArgumentException(MOVIE_NOT_FOUND));

      if (dto.movieId() != null) {
        session.setMovieId(dto.movieId());
      }

      session.setStartTime(targetStartTime, movie);
      isDirty = true;
    }

    if (isDirty) {
      sessionUoW.registerDirty(session);
      sessionUoW.commit();
    }

    return session;
  }

  @Override
  public void delete(UUID sessionId) {
    Session session = getById(sessionId);

    List<Ticket> sessionTickets = ticketRepo.findBySessionId(sessionId);

    for (Ticket ticket : sessionTickets) {
      if (ticket.getStatus() != TicketStatus.CANCELED) {
        ticket.setStatus(TicketStatus.CANCELED);
        ticketUoW.registerDirty(ticket);
      }
    }
    if (!sessionTickets.isEmpty()) {
      ticketUoW.commit();
    }

    sessionUoW.registerDeleted(session);
    sessionUoW.commit();
  }

  private void validateSessionOverlap(UUID hallId, LocalDateTime start, LocalDateTime end,
      UUID excludeSessionId) {
    List<Session> hallSessions = sessionRepo.findByHallId(hallId);

    for (Session existingSession : hallSessions) {
      if (existingSession.getId().equals(excludeSessionId)) {
        continue;
      }

      Movie existingMovie = movieRepo.findById(existingSession.getMovieId()).orElse(null);

      if (existingMovie == null) {
        continue;
      }

      LocalDateTime existingStart = existingSession.getStartTime();
      LocalDateTime existingEnd = existingStart.plusMinutes(existingMovie.getDurationMinutes());

      if (start.isBefore(existingEnd) && end.isAfter(existingStart)) {
        throw new IllegalArgumentException(
            String.format("Session overlaps with existing session for movie '%s' (%s - %s)",
                existingMovie.getTitle(),
                existingStart.toLocalTime(),
                existingEnd.toLocalTime()));
      }
    }
  }

  @Override
  public Session getById(UUID id) {
    return sessionRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + id));
  }

  @Override
  public List<Session> getByMovie(UUID movieId) {
    return sessionRepo.findByMovieId(movieId).stream()
        .sorted(Comparator.comparing(Session::getStartTime))
        .toList();
  }

  @Override
  public List<Session> getByDate(LocalDateTime date) {
    return sessionRepo.findByDate(date);
  }
}