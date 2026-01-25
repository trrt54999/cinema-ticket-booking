package com.batko.cinematicketbooking.infrastructure.data.impl.json;

import com.batko.cinematicketbooking.domain.model.Seat;
import com.batko.cinematicketbooking.infrastructure.data.repository.SeatRepository;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JsonSeatRepository extends CachedJsonRepository<Seat> implements SeatRepository {

  JsonSeatRepository(String filename) {
    super(
        filename,
        new TypeToken<ArrayList<Seat>>() {
        }.getType()
    );
  }

  @Override
  public void deleteByHallId(UUID hallId) {
    List<Seat> allSeats = new ArrayList<>(findAllInternal());

    allSeats.stream()
        .filter(seat -> seat.getHallId().equals(hallId))
        .forEach(seat -> identityMap.remove(seat.getId()));

    boolean removed = allSeats.removeIf(seat -> seat.getHallId().equals(hallId));

    if (removed) {
      writeToFile(allSeats);
      invalidateCache();
    }
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