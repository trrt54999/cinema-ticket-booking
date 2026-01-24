package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.service.dto.hall.HallStoreDto;
import com.batko.cinematicketbooking.service.dto.hall.HallUpdateDto;
import java.util.List;
import java.util.UUID;

public interface HallService {

  Hall create(HallStoreDto dto);

  Hall update(UUID id, HallUpdateDto dto);

  void delete(UUID id);

  Hall getById(UUID id);

  List<Hall> getAll();
}