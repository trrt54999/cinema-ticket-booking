package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.SessionRepository;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.dto.session.SessionStoreDto;
import com.batko.cinematicketbooking.service.dto.session.SessionUpdateDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SessionServiceImpl implements SessionService {

  private final SessionRepository sessionRepo;
  private final MovieRepository movieRepo;
  private final UnitOfWork<Session> sessionUoW;

  public SessionServiceImpl(SessionRepository sessionRepo, MovieRepository movieRepo,
      UnitOfWork<Session> sessionUoW) {
    this.sessionRepo = sessionRepo;
    this.movieRepo = movieRepo;
    this.sessionUoW = sessionUoW;
  }

  @Override
  public Session create(SessionStoreDto dto) {
    Movie movie = movieRepo.findById(dto.movieId())
        .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

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

    if (dto.hallId() != null && !dto.hallId().equals(session.getHallId())) {
      session.setHallId(dto.hallId());
      isDirty = true;
    }
    if (dto.price() != null && !dto.price().equals(session.getPrice())) {
      session.setPrice(dto.price());
      isDirty = true;
    }

    if (dto.movieId() != null || dto.startTime() != null) {
      UUID targetMovieId = (dto.movieId() != null) ? dto.movieId() : session.getMovieId();

      Movie movie = movieRepo.findById(targetMovieId)
          .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

      LocalDateTime targetStartTime =
          (dto.startTime() != null) ? dto.startTime() : session.getStartTime();

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
    sessionUoW.registerDeleted(session);
    sessionUoW.commit();
  }

  @Override
  public Session getById(UUID id) {
    return sessionRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + id));
  }

  @Override
  public List<Session> getByMovie(UUID movieId) {
    return sessionRepo.findByMovieId(movieId);
  }

  @Override
  public List<Session> getByDate(LocalDateTime date) {
    return sessionRepo.findByDate(date);
  }
}