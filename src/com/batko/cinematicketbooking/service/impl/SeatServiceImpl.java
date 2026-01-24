package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.domain.model.Seat;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.HallRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.SeatRepository;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.dto.seat.SeatStoreDto;
import com.batko.cinematicketbooking.service.dto.seat.SeatUpdateDto;
import java.util.List;
import java.util.UUID;

public class SeatServiceImpl implements SeatService {

  private final SeatRepository seatRepo;
  private final HallRepository hallRepo;
  private final UnitOfWork<Seat> seatUoW;

  public SeatServiceImpl(SeatRepository seatRepo, HallRepository hallRepo,
      UnitOfWork<Seat> seatUoW) {
    this.seatRepo = seatRepo;
    this.hallRepo = hallRepo;
    this.seatUoW = seatUoW;
  }

  @Override
  public Seat create(SeatStoreDto dto) {
    Hall hall = hallRepo.findById(dto.hallId())
        .orElseThrow(() -> new IllegalArgumentException("Hall not found"));

    if (seatRepo.findByHallIdAndRowAndNumber(dto.hallId(), dto.row(), dto.number()).isPresent()) {
      throw new IllegalArgumentException(
          "Seat already exists at Row: " + dto.row() + ", Number: " + dto.number());
    }

    Seat seat = new Seat(dto.hallId(), dto.seatType(), dto.row(), dto.number(), hall);

    seatUoW.registerNew(seat);
    seatUoW.commit();

    return seat;
  }

  @Override
  public Seat update(UUID seatId, SeatUpdateDto dto) {
    Seat seat = getById(seatId);
    boolean isDirty = false;

    if (dto.seatType() != null && dto.seatType() != seat.getSeatType()) {
      seat.setSeatType(dto.seatType());
      isDirty = true;
    }

    if (isDirty) {
      seatUoW.registerDirty(seat);
      seatUoW.commit();
    }

    return seat;
  }

  @Override
  public void delete(UUID seatId) {
    Seat seat = getById(seatId);
    seatUoW.registerDeleted(seat);
    seatUoW.commit();
  }

  @Override
  public List<Seat> getAllByHall(UUID hallId) {
    return seatRepo.findByHallId(hallId);
  }

  @Override
  public Seat getById(UUID id) {
    return seatRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Seat not found"));
  }
}
