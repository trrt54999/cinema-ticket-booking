package com.batko.cinematicketbooking.domain.data.repository;

import com.batko.cinematicketbooking.domain.model.Seat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository extends Repository<Seat> {

  List<Seat> findByHallId(UUID hallId);

  Optional<Seat> findByHallIdAndRowAndNumber(UUID hallId, int row, int number);
}