package com.batko.cinematicketbooking.infrastructure.data.repository;

import com.batko.cinematicketbooking.domain.model.Seat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository extends Repository<Seat> {

  void deleteByHallId(UUID hallId);

  List<Seat> findByHallId(UUID hallId);

  Optional<Seat> findByHallIdAndRowAndNumber(UUID hallId, int row, int number);
}