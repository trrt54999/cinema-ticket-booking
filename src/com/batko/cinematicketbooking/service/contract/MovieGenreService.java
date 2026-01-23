package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.MovieGenre;
import com.batko.cinematicketbooking.service.dto.moviegenre.MovieGenreStoreDto;
import java.util.List;
import java.util.UUID;

public interface MovieGenreService {

  void addGenreToMovie(MovieGenreStoreDto dto);

  void removeGenreFromMovie(UUID movieId, UUID genreId);

  List<MovieGenre> getGenresByMovie(UUID movieId);

  List<MovieGenre> getMoviesByGenre(UUID genreId);
}