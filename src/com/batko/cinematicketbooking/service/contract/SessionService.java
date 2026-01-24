package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.service.dto.session.SessionStoreDto;
import com.batko.cinematicketbooking.service.dto.session.SessionUpdateDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SessionService {

  Session create(SessionStoreDto dto);

  Session update(UUID sessionId, SessionUpdateDto dto);

  void delete(UUID sessionId);

  Session getById(UUID id);

  List<Session> getByMovie(UUID movieId);

  List<Session> getByDate(LocalDateTime date);
}
