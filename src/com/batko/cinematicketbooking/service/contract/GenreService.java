package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.Genre;
import com.batko.cinematicketbooking.service.dto.genre.GenreStoreDto;
import com.batko.cinematicketbooking.service.dto.genre.GenreUpdateDto;
import java.util.List;
import java.util.UUID;

public interface GenreService {

  Genre create(GenreStoreDto dto);

  Genre update(UUID id, GenreUpdateDto dto);

  void delete(UUID id);

  Genre getById(UUID id);

  List<Genre> getAll();
}