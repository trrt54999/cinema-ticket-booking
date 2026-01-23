package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.model.MovieGenre;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieGenreRepository;
import com.batko.cinematicketbooking.service.contract.MovieGenreService;
import com.batko.cinematicketbooking.service.dto.moviegenre.MovieGenreStoreDto;
import java.util.List;
import java.util.UUID;

public class MovieGenreServiceImpl implements MovieGenreService {

  private final MovieGenreRepository movieGenreRepo;
  private final UnitOfWork<MovieGenre> movieGenreUoW;

  public MovieGenreServiceImpl(MovieGenreRepository movieGenreRepo,
      UnitOfWork<MovieGenre> movieGenreUoW) {
    this.movieGenreRepo = movieGenreRepo;
    this.movieGenreUoW = movieGenreUoW;
  }

  @Override
  public void addGenreToMovie(MovieGenreStoreDto dto) {
    boolean exists = movieGenreRepo.findByMovieId(dto.movieId()).stream()
        .anyMatch(mg -> mg.getGenreId().equals(dto.genreId()));

    if (exists) {
      throw new IllegalArgumentException("This movie already has this genre assigned.");
    }

    MovieGenre movieGenre = new MovieGenre(dto.movieId(), dto.genreId());

    movieGenreUoW.registerNew(movieGenre);
    movieGenreUoW.commit();
  }

  @Override
  public void removeGenreFromMovie(UUID movieId, UUID genreId) {
    MovieGenre movieGenre = movieGenreRepo.findByMovieId(movieId).stream()
        .filter(mg -> mg.getGenreId().equals(genreId))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Genre connection not found for this movie"));

    movieGenreUoW.registerDeleted(movieGenre);
    movieGenreUoW.commit();
  }

  @Override
  public List<MovieGenre> getGenresByMovie(UUID movieId) {
    return movieGenreRepo.findByMovieId(movieId);
  }

  @Override
  public List<MovieGenre> getMoviesByGenre(UUID genreId) {
    return movieGenreRepo.findByGenreId(genreId);
  }
}