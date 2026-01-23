package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.service.dto.movie.MovieStoreDto;
import com.batko.cinematicketbooking.service.dto.movie.MovieUpdateDto;
import java.util.List;
import java.util.UUID;

public interface MovieService {

  Movie create(MovieStoreDto dto);

  Movie update(UUID movieId, MovieUpdateDto dto);

  void delete(UUID movieId);

  Movie getById(UUID id);

  List<Movie> getAll();

  void addGenre(UUID movieId, UUID genreId);

  void removeGenre(UUID movieId, UUID genreId);
}