package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.model.Genre;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.GenreRepository;
import com.batko.cinematicketbooking.service.contract.GenreService;
import com.batko.cinematicketbooking.service.dto.genre.GenreStoreDto;
import com.batko.cinematicketbooking.service.dto.genre.GenreUpdateDto;
import java.util.List;
import java.util.UUID;

public class GenreServiceImpl implements GenreService {

  private final GenreRepository genreRepo;
  private final UnitOfWork<Genre> genreUoW;

  public GenreServiceImpl(GenreRepository genreRepo, UnitOfWork<Genre> genreUoW) {
    this.genreRepo = genreRepo;
    this.genreUoW = genreUoW;
  }

  @Override
  public Genre create(GenreStoreDto dto) {
    if (genreRepo.existsByName(dto.name())) {
      throw new IllegalArgumentException("Genre with name '" + dto.name() + "' already exists");
    }

    Genre genre = new Genre(dto.name());

    genreUoW.registerNew(genre);
    genreUoW.commit();

    return genre;
  }

  @Override
  public Genre update(UUID id, GenreUpdateDto dto) {
    Genre genre = getById(id);
    boolean isDirty = false;

    if (dto.name() != null && !dto.name().isBlank() && !dto.name()
        .equalsIgnoreCase(genre.getName())) {

      if (genreRepo.existsByName(dto.name())) {
        throw new IllegalArgumentException("Genre with name '" + dto.name() + "' already exists");
      }

      genre.setName(dto.name());
      isDirty = true;
    }

    if (isDirty) {
      genreUoW.registerDirty(genre);
      genreUoW.commit();
    }

    return genre;
  }

  @Override
  public void delete(UUID id) {
    Genre genre = getById(id);
    genreUoW.registerDeleted(genre);
    genreUoW.commit();
  }

  @Override
  public Genre getById(UUID id) {
    return genreRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Genre not found"));
  }

  @Override
  public List<Genre> getAll() {
    return genreRepo.findAll();
  }
}
