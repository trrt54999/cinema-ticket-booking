package com.batko.cinematicketbooking.domain.data.repository;

import com.batko.cinematicketbooking.domain.model.MovieGenre;
import java.util.List;
import java.util.UUID;

public interface MovieGenreRepository extends Repository<MovieGenre> {

  List<MovieGenre> findByMovieId(UUID movieId);

  List<MovieGenre> findByGenreId(UUID genreId);
}
