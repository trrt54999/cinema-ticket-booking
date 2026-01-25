package com.batko.cinematicketbooking.infrastructure.data.impl.json;

import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.infrastructure.data.repository.SessionRepository;
import com.google.gson.reflect.TypeToken;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JsonSessionRepository extends CachedJsonRepository<Session> implements
    SessionRepository {

  JsonSessionRepository(String filename) {
    super(
        filename,
        new TypeToken<ArrayList<Session>>() {
        }.getType()
    );
  }

  @Override
  public List<Session> findByMovieId(UUID movieId) {
    return findBy(session -> session.getMovieId().equals(movieId));
  }

  @Override
  public List<Session> findByHallId(UUID hallId) {
    return findBy(session -> session.getHallId().equals(hallId));
  }

  @Override
  public List<Session> findByDate(LocalDateTime date) {
    return findBy(session -> session.getStartTime().equals(date.toLocalDate()));
  }
}