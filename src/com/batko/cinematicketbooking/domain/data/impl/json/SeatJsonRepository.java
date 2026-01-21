package com.batko.cinematicketbooking.domain.data.impl.json;

import com.batko.cinematicketbooking.domain.data.repository.SeatRepository;
import com.batko.cinematicketbooking.domain.model.Seat;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SeatJsonRepository extends CachedJsonRepository<Seat> implements SeatRepository {

  public SeatJsonRepository(String filename) {
    super(
        filename,
        new TypeToken<ArrayList<Seat>>() {
        }.getType()
    );
  }

  @Override
  public List<Seat> findByHallId(UUID hallId) {
    return findBy(seat -> seat.getHallId().equals(hallId));
  }

  @Override
  public Optional<Seat> findByHallIdAndRowAndNumber(UUID hallId, int row, int number) {
    return findFirstBy(seat ->
        seat.getHallId().equals(hallId) &&
            seat.getRow() == row &&
            seat.getNumber() == number
    );
  }
}