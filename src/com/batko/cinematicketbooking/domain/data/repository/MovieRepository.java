package com.batko.cinematicketbooking.domain.data.repository;

import com.batko.cinematicketbooking.domain.model.Movie;
import java.util.List;
import java.util.UUID;

public interface MovieRepository extends Repository<Movie> {

  List<Movie> findByTitle(String title);

  boolean existsByTitle(String title);

  List<Movie> findByManagerId(UUID managerId);
}