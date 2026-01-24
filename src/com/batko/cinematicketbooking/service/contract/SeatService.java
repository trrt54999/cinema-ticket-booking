package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.Seat;
import com.batko.cinematicketbooking.service.dto.seat.SeatStoreDto;
import com.batko.cinematicketbooking.service.dto.seat.SeatUpdateDto;
import java.util.List;
import java.util.UUID;

public interface SeatService {

  Seat create(SeatStoreDto dto);

  Seat update(UUID seatId, SeatUpdateDto dto);

  void delete(UUID seatId);

  List<Seat> getAllByHall(UUID hallId);

  Seat getById(UUID id);
}