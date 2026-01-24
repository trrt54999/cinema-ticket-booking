package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.HallRepository;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.dto.hall.HallStoreDto;
import com.batko.cinematicketbooking.service.dto.hall.HallUpdateDto;
import java.util.List;
import java.util.UUID;

public class HallServiceImpl implements HallService {

  private final HallRepository hallRepo;
  private final UnitOfWork<Hall> hallUoW;

  public HallServiceImpl(HallRepository hallRepo, UnitOfWork<Hall> hallUoW) {
    this.hallRepo = hallRepo;
    this.hallUoW = hallUoW;
  }

  @Override
  public Hall create(HallStoreDto dto) {

    if (hallRepo.findByName(dto.name()).isPresent()) {
      throw new IllegalArgumentException("Hall with name '" + dto.name() + "' already exists");
    }

    Hall hall = new Hall(dto.name(), dto.rows(), dto.seatsPerRow());

    hallUoW.registerNew(hall);
    hallUoW.commit();

    return hall;
  }

  @Override
  public Hall update(UUID id, HallUpdateDto dto) {
    Hall hall = getById(id);
    boolean isDirty = false;

    if (dto.name() != null && !dto.name().isBlank() && !dto.name().equals(hall.getName())) {
      if (hallRepo.findByName(dto.name()).isPresent()) {
        throw new IllegalArgumentException("Hall with name '" + dto.name() + "' already exists");
      }
      hall.setName(dto.name());
      isDirty = true;
    }

    if (dto.rows() != null && dto.rows() != hall.getRows()) {
      hall.setRows(dto.rows());
      isDirty = true;
    }

    if (dto.seatsPerRow() != null && dto.seatsPerRow() != hall.getSeatsPerRow()) {
      hall.setSeatsPerRow(dto.seatsPerRow());
      isDirty = true;
    }

    if (isDirty) {
      hallUoW.registerDirty(hall);
      hallUoW.commit();
    }

    return hall;
  }

  @Override
  public void delete(UUID id) {
    Hall hall = getById(id);

    hallUoW.registerDeleted(hall);
    hallUoW.commit();
  }

  @Override
  public Hall getById(UUID id) {
    return hallRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Hall not found"));
  }

  @Override
  public List<Hall> getAll() {
    return hallRepo.findAll();
  }
}
