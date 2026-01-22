package com.batko.cinematicketbooking.infrastructure.data.repository;

import com.batko.cinematicketbooking.domain.model.Genre;
import java.util.Optional;

public interface GenreRepository extends Repository<Genre> {

  Optional<Genre> findByName(String name);

  boolean existsByName(String name);
}