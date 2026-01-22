package com.batko.cinematicketbooking.infrastructure.data.repository;

import com.batko.cinematicketbooking.domain.model.Session;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SessionRepository extends Repository<Session> {

  List<Session> findByMovieId(UUID movieId);

  List<Session> findByHallId(UUID hallId);

  List<Session> findByDate(LocalDateTime date);
}